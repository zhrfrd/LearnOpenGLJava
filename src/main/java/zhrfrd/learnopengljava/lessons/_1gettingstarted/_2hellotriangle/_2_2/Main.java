/**
 * Make a rectangle
 * https://learnopengl.com/Getting-started/Hello-Triangle
 */

package zhrfrd.learnopengljava.lessons._1gettingstarted._2hellotriangle._2_2;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public class Main {
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

    private static final String fragmentShaderSource =
            "#version 330 core\n" +
                    "out vec4 FragColor;\n" +   // Fragment shader only requires one output variable.
                    "void main()\n" +
                    "{\n" +
                    "   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
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

        int fragmentShader = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);   // Fragment shader is responsible for the colouring of the scene.
        GL33.glShaderSource(fragmentShader, fragmentShaderSource);
        GL33.glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        // LINK THE SHADER OBJECTS TO THE SHADER PROGRAM
        // To use the recently compiled shaders we have to link them to a shader program object and then activate this shader program when rendering objects. The activated shader program's shaders will be used when we issue render calls.
        // When linking the shaders into a program it links the outputs of each shader to the inputs of the next shader.
        int shaderProgram = GL33.glCreateProgram();   // Create program object. Return 0 if there is an error creating the program.
        GL33.glAttachShader(shaderProgram, vertexShader);   // Attach the compiled shader objects to the program object.
        GL33.glAttachShader(shaderProgram, fragmentShader); //
        GL33.glLinkProgram(shaderProgram);   // Links all the attached compiled shaders  to the program object
        checkLinkErrors(shaderProgram);   // Check if linking the shaders to the program caused an error.
        GL33.glDeleteShader(vertexShader);   // De-allocate resources.
        GL33.glDeleteShader(fragmentShader); //

        // LINKING VERTEX ATTRIBUTES
        // Tell OpenGL how it should interpret the vertex data in memory and how it should connect the vertex data to the vertex shader's attributes.

        // Set up vertex data and configure vertex attributes
        float vertices[] = {
                0.5f,  0.5f, 0.0f,  // top right
                0.5f, -0.5f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f,  // bottom left
                -0.5f,  0.5f, 0.0f   // top left
        };
        int indices[] = {  // note that we start from 0!
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };

        int VAO = GL33.glGenVertexArrays();   // glGenVertexArrays Generate a vertex array object
        int VBO = GL33.glGenBuffers();   // glGenBuffers Generates a buffer object. Vertex Buffer Object
        int EBO = GL33.glGenBuffers();   // Element Buffer Object

        GL33.glBindVertexArray(VAO);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, VBO);   // Bind the buffer object created (VBO) to the specified buffer type (GL_ARRAY_BUFFER). From that point on any buffer calls we make (on the GL_ARRAY_BUFFER target) will be used to configure the currently bound buffer, which is VBO.
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);   // Copy the previously defined vertex data into the buffers memory.
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, EBO);   // Bind the buffer object created (EBO) to the specified buffer type (GL_ARRAY_BUFFER).
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, indices, GL33.GL_STATIC_DRAW);   // Copy the previously defined vertex data into the buffers memory.

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 3 * Float.BYTES, 0);   // Vertex attribute configuration.
        GL33.glEnableVertexAttribArray(0);   // Enable the vertex attribute

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);

//       GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);   // Wireframe mode

        // Render loop
        while (!GLFW.glfwWindowShouldClose(window)) {
            processInput(window);

            // Render
            GL33.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);

            GL33.glUseProgram(shaderProgram);   // Activate the shader program for subsequent drawing commands.
            GL33.glBindVertexArray(VAO);
            GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        // De-allocate resources
        GL33.glDeleteVertexArrays(VAO);
        GL33.glDeleteBuffers(VBO);
        GL33.glDeleteBuffers(EBO);
        GL33.glDeleteProgram(shaderProgram);

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
