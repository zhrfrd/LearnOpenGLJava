package zhrfrd.learnopengl.lessons._1gettingstarted._5transofmations._5_2;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private long window;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        init();
        loop();

        // Free resources and close window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Initialize GLFW
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "LWJGL Texture Example", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set the resize callback
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // Load OpenGL functions
        GL.createCapabilities();
    }

    private void loop() {
        Shader shader = new Shader("resources/shaders/5.1.transform.vert", "resources/shaders/5.1.transform.frag");

        // Set up vertex data
        float[] vertices = {
                // positions          // texture coords
                0.5f,  0.5f, 0.0f,   1.0f, 1.0f, // top right
                0.5f, -0.5f, 0.0f,   1.0f, 0.0f, // bottom right
                -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, // bottom left
                -0.5f,  0.5f, 0.0f,   0.0f, 1.0f  // top left
        };
        int[] indices = {
                0, 1, 3, // first triangle
                1, 2, 3  // second triangle
        };

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // Texture coordinate attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // TEXTURE 1
        // Load and create a texture
        int texture1 = glGenTextures();
        glActiveTexture(GL_TEXTURE0);   // activate the texture unit first before binding texture. Texture unit GL_TEXTURE0 is always by default activated, so we didn't have to activate any texture units in the previous example when using glBindTexture.
        glBindTexture(GL_TEXTURE_2D, texture1);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Load image
        loadImage("resources/textures/container.jpg", GL_RGB);

        // TEXTURE 2
        int texture2 = glGenTextures();
        glActiveTexture(GL_TEXTURE1);   // activate the texture unit first before binding texture. Texture unit GL_TEXTURE0 is always by default activated, so we didn't have to activate any texture units in the previous example when using glBindTexture.
        glBindTexture(GL_TEXTURE_2D, texture2);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Load image
        loadImage("resources/textures/awesomeface.png", GL_RGBA);

        shader.use();
        glUniform1i(glGetUniformLocation(shader.getId(), "texture1"), 0);
        glUniform1i(glGetUniformLocation(shader.getId(), "texture2"), 1);

        // Render loop
        while (!glfwWindowShouldClose(window)) {
            processInput(window);

            // Clear screen
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Render texture
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture1);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, texture2);

            // TRANSFORMATION
            Matrix4f transform = new Matrix4f();
            transform.translate(0.5f, -0.5f, 0.0f);
            transform.rotate((float) glfwGetTime(), 0.0f, 0.0f, 1.0f);


            int transformLoc = glGetUniformLocation(shader.getId(), "transform");

            // Create a FloatBuffer to store the matrix data
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer transformBuffer = stack.mallocFloat(16);
                transform.get(transformBuffer); // Fill the buffer with matrix data
                glUniformMatrix4fv(transformLoc, false, transformBuffer); // Send it to the shader
            }

            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Clean up
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

    private static void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private void loadImage(String path, int format) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer imageData = stbi_load(path, width, height, channels, 0);
            if (imageData != null) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, format, GL_UNSIGNED_BYTE, imageData);
                glGenerateMipmap(GL_TEXTURE_2D);
                stbi_image_free(imageData);
            } else {
                System.err.println("Failed to load texture");
            }
        }
    }
}
