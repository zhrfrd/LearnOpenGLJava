/**
 * Use shaders to change the triangle colour
 * https://learnopengl.com/Getting-started/Shaders
 */

package zhrfrd.learnopengl.lessons._1gettingstarted._3shaders._3_1;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private static final int SCR_WIDTH = 800;
    private static final int SCR_HEIGHT = 600;

    private static final String vertexShaderSource =
            "#version 330 core\n" +
                    "layout (location = 0) in vec3 aPos;\n" +
                    "out vec4 vertexColor;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   gl_Position = vec4(aPos, 1.0);\n" +
                    "   vertexColor = vec4(0.5, 0.0, 0.0, 1.0);\n" +
                    "}\n";

    private static final String fragmentShaderSource =
            "#version 330 core\n" +
                    "out vec4 FragColor;\n" +
                    "in vec4 vertexColor;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   FragColor = vertexColor;\n" +
                    "}\n";

    private long window;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });
        glfwSwapInterval(1);
        GL.createCapabilities();
    }

    private void loop() {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkLinkingErrors(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        float[] vertices = {
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };

        int VAO = glGenVertexArrays();
        int VBO = glGenBuffers();
        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        while (!glfwWindowShouldClose(window)) {
            processInput(window);
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glUseProgram(shaderProgram);

            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteProgram(shaderProgram);
    }

    private void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private void checkCompileErrors(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println("ERROR::SHADER::" + type + "::COMPILATION_FAILED\n" + glGetShaderInfoLog(shader));
        }
    }

    private void checkLinkingErrors(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.out.println("ERROR::SHADER::PROGRAM::LINKING_FAILED\n" + glGetProgramInfoLog(program));
        }
    }
}
