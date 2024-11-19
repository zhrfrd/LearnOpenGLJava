/**
 * https://learnopengl.com/Getting-started/Hello-Window
 */

package zhrfrd.learnopengl.lessons.hellotriangle;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class A1HelloWindow {

    private static final int SCR_WIDTH = 800;
    private static final int SCR_HEIGHT = 600;

    public static void main(String[] args) {
        // Configure GLFW
        GLFW.glfwInit();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        // For MacOS compatibility
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }

        // Create the window
        long window = GLFW.glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", 0, 0);
        if (window == 0) {
            System.out.println("Failed to create GLFW window");
            GLFW.glfwTerminate();
            return;
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the framebuffer size callback
        GLFW.glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                GL11.glViewport(0, 0, width, height);
            }
        });

        // Render loop
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Input
            processInput(window);

            // Render
            GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            // Swap buffers and poll events
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
    }

    private static void processInput(long window) {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }
}
