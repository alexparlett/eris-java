package org.homonoia.eris.graphics.drawables.sp;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL20.GL_BOOL;
import static org.lwjgl.opengl.GL20.GL_BOOL_VEC2;
import static org.lwjgl.opengl.GL20.GL_BOOL_VEC3;
import static org.lwjgl.opengl.GL20.GL_BOOL_VEC4;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT4;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC4;
import static org.lwjgl.opengl.GL20.GL_INT_VEC2;
import static org.lwjgl.opengl.GL20.GL_INT_VEC3;
import static org.lwjgl.opengl.GL20.GL_INT_VEC4;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_VEC2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_VEC3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_VEC4;
import static org.lwjgl.opengl.GL40.glUniform1d;
import static org.lwjgl.opengl.GL40.glUniform2d;
import static org.lwjgl.opengl.GL40.glUniform3d;
import static org.lwjgl.opengl.GL40.glUniform4d;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Created by alexparlett on 16/04/2016.
 */
public class Uniform {
    private int type;
    private int location;
    private Object data;

    private Uniform(Builder builder) {
        this.type = builder.type;
        this.location = builder.location;
        this.data = builder.data;
    }

    public int getType() {
        return type;
    }

    public int getLocation() {
        return location;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public static final class Builder {

        private int type;
        private int location;
        private Object data;
        private Builder() {
        }

        public Uniform build() {
            return new Uniform(this);
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder location(int location) {
            this.location = location;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

    }
    public void bindUniform() {
        bindUniform(this.data);
    }

    public void bindUniform(Object bindingData) {
        try (MemoryStack stack = stackPush()) {
            switch (type) {
                case GL_FLOAT:
                    Float fData = (Float) bindingData;
                    glUniform1f(location, fData);
                    break;
                case GL_FLOAT_VEC2:
                    Vector2f v2fData = (Vector2f) bindingData;
                    glUniform2f(location, v2fData.x, v2fData.y);
                    break;
                case GL_FLOAT_VEC3:
                    Vector3f v3fData = (Vector3f) bindingData;
                    glUniform3f(location, v3fData.x, v3fData.y, v3fData.z);
                    break;
                case GL_FLOAT_VEC4:
                    Vector4f v4fData = (Vector4f) bindingData;
                    glUniform4f(location, v4fData.x, v4fData.y, v4fData.z, v4fData.w);
                    break;
                case GL_DOUBLE:
                    Double dData = (Double) bindingData;
                    glUniform1d(location, dData);
                    break;
                case GL_DOUBLE_VEC2:
                    Vector2d v2dData = (Vector2d) bindingData;
                    glUniform2d(location, v2dData.x, v2dData.y);
                    break;
                case GL_DOUBLE_VEC3:
                    Vector3d v3dData = (Vector3d) bindingData;
                    glUniform3d(location, v3dData.x, v3dData.y, v3dData.z);
                    break;
                case GL_DOUBLE_VEC4:
                    Vector4d v4dData = (Vector4d) bindingData;
                    glUniform4d(location, v4dData.x, v4dData.y, v4dData.z, v4dData.w);
                    break;
                case GL_INT:
                    Integer iData = (Integer) bindingData;
                    glUniform1i(location, iData);
                    break;
                case GL_INT_VEC2:
                case GL_BOOL_VEC2:
                    Vector2i v2iData = (Vector2i) bindingData;
                    glUniform2i(location, v2iData.x, v2iData.y);
                    break;
                case GL_INT_VEC3:
                case GL_BOOL_VEC3:
                    Vector3i v3iData = (Vector3i) bindingData;
                    glUniform3i(location, v3iData.x, v3iData.y, v3iData.z);
                    break;
                case GL_INT_VEC4:
                case GL_BOOL_VEC4:
                    Vector4i v4iData = (Vector4i) bindingData;
                    glUniform4i(location, v4iData.x, v4iData.y, v4iData.z, v4iData.w);
                    break;
                case GL_BOOL:
                    Boolean bData = (Boolean) bindingData;
                    glUniform1i(location, bData ? 1 : 0);
                    break;
                case GL_FLOAT_MAT3:
                    Matrix3f m3f = (Matrix3f) bindingData;
                    FloatBuffer m3fb = m3f.get(stack.mallocFloat(9));
                    glUniformMatrix3fv(location, false, m3fb);
                    break;
                case GL_FLOAT_MAT4:
                    Matrix4f m4f = (Matrix4f) bindingData;
                    FloatBuffer m4fb = m4f.get(stack.mallocFloat(16));
                    glUniformMatrix4fv(location, false, m4fb);
                    break;
            }
        }
    }
}
