package zhrfrd.learnopengl.lessons.hellotriangle.exercises;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public class Exercise3 {

    // Settings
    private static final int SCR_WIDTH = 800;
    private static final int SCR_HEIGHT = 600;

    // Shader sources
    private static final String vertexShaderSource =
            "#version 330 core\n" +
                    "layout (location = 0) in vec3 aPos;\n" +   // Input vec3 with the name aPos in location 0
                    "void main()\n" +
                    "{\n" +
                    "   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +   // assign vertex shader output to gl_Position
                    "}";

    private static final String fragmentShaderSource1 =
            "#version 330 core\n" +
                    "out vec4 FragColor;\n" +   // Fragment shader only requires one output variable.
                    "void main()\n" +
                    "{\n" +
                    "   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
                    "}";

    private static final String fragmentShaderSource2 =
            "#version 330 core\n" +
                    "out vec4 FragColor;\n" +   // Fragment shader only requires one output variable.
                    "void main()\n" +
                    "{\n" +
                    "   FragColor = vec4(0.0f, 0.5f, 0.2f, 1.0f);\n" +
                    "}";

    public static void main(String[] args) {
        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        // Configure GLFW
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        // Create a windowed mode window and its OpenGL context
        long window = GLFW.glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            System.out.println("Failed to create GLFW window");
            GLFW.glfwTerminate();
            return;
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                GL33.glViewport(0, 0, width, height);
            }
        });

        GL.createCapabilities();

        // BUILD AND COMPILE THE SHADERS
        int vertexShader = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);   // Create Vertex shader object.
        GL33.glShaderSource(vertexShader, vertexShaderSource);   // Pass the shader source to the vertex shader object created above.
        GL33.glCompileShader(vertexShader);   // Compile the shader
        checkCompileErrors(vertexShader, "VERTEX");   // Check if the compilation of the shader succeded.

        int fragmentShader1 = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);   // Fragment shader is responsible for the colouring of the scene.
        GL33.glShaderSource(fragmentShader1, fragmentShaderSource1);
        GL33.glCompileShader(fragmentShader1);
        checkCompileErrors(fragmentShader1, "FRAGMENT1");

        int fragmentShader2 = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);   // Fragment shader is responsible for the colouring of the scene.
        GL33.glShaderSource(fragmentShader2, fragmentShaderSource2);
        GL33.glCompileShader(fragmentShader2);
        checkCompileErrors(fragmentShader2, "FRAGMENT2");

        // LINK THE SHADER OBJECTS TO THE SHADER PROGRAM
        // To use the recently compiled shaders we have to link them to a shader program object and then activate this shader program when rendering objects. The activated shader program's shaders will be used when we issue render calls.
        // When linking the shaders into a program it links the outputs of each shader to the inputs of the next shader.
        int shaderProgram1 = GL33.glCreateProgram();   // Create program object. Return 0 if there is an error creating the program.
        GL33.glAttachShader(shaderProgram1, vertexShader);   // Attach the compiled shader objects to the program object.
        GL33.glAttachShader(shaderProgram1, fragmentShader1); //
        GL33.glLinkProgram(shaderProgram1);   // Links all the attached compiled shaders  to the program object
        checkLinkErrors(shaderProgram1);   // Check if linking the shaders to the program caused an error.
        int shaderProgram2 = GL33.glCreateProgram();   // Create program object. Return 0 if there is an error creating the program.
        GL33.glAttachShader(shaderProgram2, vertexShader);   // Attach the compiled shader objects to the program object.
        GL33.glAttachShader(shaderProgram2, fragmentShader2); //
        GL33.glLinkProgram(shaderProgram2);   // Links all the attached compiled shaders  to the program object
        checkLinkErrors(shaderProgram2);   // Check if linking the shaders to the program caused an error.

        GL33.glDeleteShader(vertexShader);   // De-allocate resources.
        GL33.glDeleteShader(fragmentShader1); //
        GL33.glDeleteShader(fragmentShader2); //

        // LINKING VERTEX ATTRIBUTES
        // Tell OpenGL how it should interpret the vertex data in memory and how it should connect the vertex data to the vertex shader's attributes.

        // Set up vertex data and configure vertex attributes
        // First triangle
        float[] vertices1 = {
                -1f, -0.5f, 0.0f,
                0f, -0.5f, 0.0f,
                -0.5f,  0.5f, 0.0f,
        };
        // Second triangle
        float[] vertices2 = {
                -0f, -0.5f, 0.0f,
                1f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
        };

        int VAO1 = GL33.glGenVertexArrays();   // glGenVertexArrays Generate a vertex array object
        int VBO1 = GL33.glGenBuffers();   // glGenBuffers Generates a buffer object.
        int VAO2 = GL33.glGenVertexArrays();   // glGenVertexArrays Generate a vertex array object
        int VBO2 = GL33.glGenBuffers();   // glGenBuffers Generates a buffer object.

        // First Triangle
        GL33.glBindVertexArray(VAO1);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, VBO1);   // Bind the buffer object created (VBO) to the specified buffer type (GL_ARRAY_BUFFER). From that point on any buffer calls we make (on the GL_ARRAY_BUFFER target) will be used to configure the currently bound buffer, which is VBO.
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices1, GL33.GL_STATIC_DRAW);   // Copy the previously defined vertex data into the buffers memory.

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 3 * Float.BYTES, 0);   // Vertex attribute configuration.
        GL33.glEnableVertexAttribArray(0);   // Enable the vertex attribute

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);

        // Second triangle
        GL33.glBindVertexArray(VAO2);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, VBO2);   // Bind the buffer object created (VBO) to the specified buffer type (GL_ARRAY_BUFFER). From that point on any buffer calls we make (on the GL_ARRAY_BUFFER target) will be used to configure the currently bound buffer, which is VBO.
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices2, GL33.GL_STATIC_DRAW);   // Copy the previously defined vertex data into the buffers memory.

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 3 * Float.BYTES, 0);   // Vertex attribute configuration.
        GL33.glEnableVertexAttribArray(0);   // Enable the vertex attribute

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);
        // Render loop
        while (!GLFW.glfwWindowShouldClose(window)) {
            processInput(window);

            // Render
            GL33.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);

            GL33.glUseProgram(shaderProgram1);   // Activate the shader program for subsequent drawing commands.
            GL33.glBindVertexArray(VAO1);
            GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);
            GL33.glUseProgram(shaderProgram2);   // Activate the shader program for subsequent drawing commands.
            GL33.glBindVertexArray(VAO2);
            GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        // De-allocate resources
        GL33.glDeleteVertexArrays(VAO1);
        GL33.glDeleteBuffers(VBO1);
        GL33.glDeleteVertexArrays(VAO2);
        GL33.glDeleteBuffers(VBO2);
        GL33.glDeleteProgram(shaderProgram1);
        GL33.glDeleteProgram(shaderProgram2);

        GLFW.glfwTerminate();
    }

    private static void processInput(long window) {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }

    /**
     * Check if the specified shader failed to compile.
     * @param shader Current shader.
     * @param type Type of shader.
     */
    private static void checkCompileErrors(int shader, String type) {
        int success = GL33.glGetShaderi(shader, GL33.GL_COMPILE_STATUS);
        if (success == 0) {
            String infoLog = GL33.glGetShaderInfoLog(shader);
            System.out.println("ERROR::SHADER::" + type + "::COMPILATION_FAILED\n" + infoLog);
        }
    }

    /**
     * Check if the linking of the shader objects to the program object cause any errors.
     * @param program The program object to check.
     */
    private static void checkLinkErrors(int program) {
        int success = GL33.glGetProgrami(program, GL33.GL_LINK_STATUS);
        if (success == 0) {
            String infoLog = GL33.glGetProgramInfoLog(program);
            System.out.println("ERROR::SHADER::PROGRAM::LINKING_FAILED\n" + infoLog);
        }
    }
}
