package zhrfrd.learnopengljava;

import org.lwjgl.opengl.GL33;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader {
    private int id; // Program ID

    // Constructor generates the shader on the fly
    public Shader(String vertexPath, String fragmentPath) {
        try {
            // 1. Retrieve the vertex/fragment source code from filePath
            String vertexCode = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + vertexPath)));
            String fragmentCode = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + fragmentPath)));

            // 2. Compile shaders
            int vertex = compileShader(vertexCode, GL33.GL_VERTEX_SHADER, "VERTEX");
            int fragment = compileShader(fragmentCode, GL33.GL_FRAGMENT_SHADER, "FRAGMENT");

            // Shader Program
            id = GL33.glCreateProgram();
            GL33.glAttachShader(id, vertex);
            GL33.glAttachShader(id, fragment);
            GL33.glLinkProgram(id);
            checkCompileErrors(id, "PROGRAM");

            // Delete the shaders as they're linked into our program now and no longer necessary
            GL33.glDeleteShader(vertex);
            GL33.glDeleteShader(fragment);
        } catch (Exception e) {
            System.err.println("ERROR::SHADER::FILE_NOT_SUCCESSFULLY_READ");
            e.printStackTrace();
        }
    }

    // Activate the shader
    public void use() {
        GL33.glUseProgram(id);
    }

    public int getId() {
        return id;
    }

    // Utility uniform functions
    public void setBool(String name, boolean value) {
        GL33.glUniform1i(GL33.glGetUniformLocation(id, name), value ? 1 : 0);
    }

    public void setInt(String name, int value) {
        GL33.glUniform1i(GL33.glGetUniformLocation(id, name), value);
    }

    public void setFloat(String name, float value) {
        GL33.glUniform1f(GL33.glGetUniformLocation(id, name), value);
    }

    // Helper method to compile a shader
    private int compileShader(String shaderCode, int type, String shaderTypeName) {
        int shader = GL33.glCreateShader(type);
        GL33.glShaderSource(shader, shaderCode);
        GL33.glCompileShader(shader);
        checkCompileErrors(shader, shaderTypeName);
        return shader;
    }

    // Utility function for checking shader compilation/linking errors
    private void checkCompileErrors(int object, String type) {
        if (!type.equals("PROGRAM")) {
            if (GL33.glGetShaderi(object, GL33.GL_COMPILE_STATUS) == GL33.GL_FALSE) {
                System.err.println("ERROR::SHADER_COMPILATION_ERROR of type: " + type);
                System.err.println(GL33.glGetShaderInfoLog(object));
            }
        } else {
            if (GL33.glGetProgrami(object, GL33.GL_LINK_STATUS) == GL33.GL_FALSE) {
                System.err.println("ERROR::PROGRAM_LINKING_ERROR of type: " + type);
                System.err.println(GL33.glGetProgramInfoLog(object));
            }
        }
    }
}
