package zhrfrd.learnopengl;

import org.lwjgl.opengl.*;
import zhrfrd.learnopengl.lessons.shaders.Shaders2;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    // Settings
    private static final int SCR_WIDTH = 800;
    private static final int SCR_HEIGHT = 600;
    private long window;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        init();
        render();

        // Terminate GLFW
        glfwTerminate();
    }

    private void init() {
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        // Configure GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // MacOS compatibility
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        // Create GLFW window
        window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", NULL, NULL);
        if (window == NULL) {
            System.err.println("Failed to create GLFW window");
            glfwTerminate();
            return;
        }

        // Make the OpenGL context current and set framebuffer resize callback
        glfwMakeContextCurrent(window);
        glfwSetFramebufferSizeCallback(window, (win, width, height) -> glViewport(0, 0, width, height));

        // Load OpenGL functions using GL.createCapabilities
        GL.createCapabilities();
    }

    private void render() {
        // Build and compile the shader program
        Shader shader = new Shader("/shaders/shader.vert", "/shaders/shader.frag");

        // Set up vertex data and buffers
        float[] vertices = {
                // positions         // colors
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,  // bottom left
                0.0f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f   // top
        };

        int VAO = glGenVertexArrays();
        int VBO = glGenBuffers();

        // Bind and configure VAO and VBO
        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Render loop
        while (!glfwWindowShouldClose(window)) {
            // Input handling
            processInput(window);

            // Rendering
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

//            float offset = 0.5f;
//            shader.setFloat("xOffset", offset);

            double timeValue = glfwGetTime();
            float greenValue = (float) (Math.sin(timeValue) / 2.0f + 0.5f);

            int vertexColorLocation = glGetUniformLocation(shader.getId(), "currentColor");   // Retrieve the uniform location of a given shader program and uniform name.
            glUniform4f(vertexColorLocation, 0.0f, greenValue, 0.0f, 1.0f);   // Set a uniform value of the current active shader program

            // Render the triangle
            shader.use();
            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Cleanup resources
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
    }

    // Process all input
    private static void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }
}
