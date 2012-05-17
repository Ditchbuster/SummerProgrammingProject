/**
 * Original author Finomosec 
 * code found on http://jmonkeyengine.org/groups/development-discussion-jme3/forum/topic/new-class-for-custom-meshes/?#post-175854 on 5/17/12
 * 
 * Edited for own use by: Ditchbuster - trioman008@hotmail.com
 * Chris Pearson
 */


package gameclient;
//package com.jme3.scene;

import java.nio.*;
import java.util.*;
import java.util.Map.Entry;
 
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
 
/**
 * This helper class simplifies the creation of custom meshes. It is built so you can add vertex by vertex.<br>
 * It dynamically initializes the needed buffers and manages them automatically when adding and setting the first vertex.<br>
 * By adding a second vertex and not specifying the values for all buffers the values from the previous vertex will be copied automatically.<br>
 * When done adding vertexes and before you can use the mesh you need to call the method {@link #finish()}.
 *
 * @author Finomosec
 */
public class CustomMesh extends Mesh {
 
    private final int initialSize;
    private final int increaseBy = 50;
 
    private int index = 0;
 
    /**
     * Dynamically managed buffers
     */
    private Map<Type, Buffer> buffers = new HashMap<Type, Buffer>();
 
    /**
     * Here we store the values set by the set methods for use in {@link #addVertex()}.
     * That way we preserve the prior value and thus can reuse it when the individual value is not changed.
     * Example: We want 9 vertexes in blue. Call {@link #setColor(ColorRGBA)} with blue.
     * Call {@link #addVertex(Vector3f)} 9 times with the wanted vertexes.
     */
    private Map<Type, Object> values = new HashMap<Type, Object>();
 
    /**
     * @param initialSize
     *            number of Vertexes expected (will increase automatically if needed, but with loss of performance)
     */
    public CustomMesh(int initialSize) {
        this.initialSize = initialSize;
    }
 
    private Buffer getBufferFor(Type type) {
        Buffer buffer = buffers.get(type);
        TypeMeta typeMeta = TypeMeta.getTypeMeta(type);
        if (buffer == null) {
            buffer = typeMeta.createBuffer(initialSize);
            buffers.put(type, buffer);
        } else if (buffer.remaining() < typeMeta.getNumValues()) {
            if (buffer instanceof FloatBuffer) {
                buffer = BufferUtils.ensureLargeEnough((FloatBuffer) buffer, typeMeta.getNumValues() * increaseBy);
            } else if (buffer instanceof IntBuffer) {
                buffer = ensureLargeEnough((IntBuffer) buffer, typeMeta.getNumValues() * increaseBy);
            } else if (buffer instanceof ShortBuffer) {
                buffer = BufferUtils.ensureLargeEnough((ShortBuffer) buffer, typeMeta.getNumValues() * increaseBy);
            } else if (buffer instanceof FloatBuffer) {
                buffer = BufferUtils.ensureLargeEnough((FloatBuffer) buffer, typeMeta.getNumValues() * increaseBy);
            }
            buffers.put(type, buffer);
        }
        return buffer;
    }
 
    /**
     * Ensures there is at least the <code>required</code> number of entries left after the current position of the
     * buffer. If the buffer is too small a larger one is created and the old one copied to the new buffer.
     *
     * @param buffer
     *            buffer that should be checked/copied (may be null)
     * @param required
     *            minimum number of elements that should be remaining in the returned buffer
     * @return a buffer large enough to receive at least the <code>required</code> number of entries, same position as
     *         the input buffer, not null
     */
    public static IntBuffer ensureLargeEnough(IntBuffer buffer, int required) { // Did not exist in BufferUtils so i had to copy & modify it TODO: Add/move to BufferUtils
        if (buffer == null || (buffer.remaining() < required)) {
            int position = (buffer != null ? buffer.position() : 0);
            IntBuffer newVerts = BufferUtils.createIntBuffer(position + required);
            if (buffer != null) {
                buffer.rewind();
                newVerts.put(buffer);
                newVerts.position(position);
            }
            buffer = newVerts;
        }
        return buffer;
    }
 
    public void addVertex() {
        ((IntBuffer) getBufferFor(Type.Index)).put(index++);
        for (Entry<Type, Object> valueEntry : values.entrySet()) {
            Buffer buffer = getBufferFor(valueEntry.getKey());
            if (buffer instanceof FloatBuffer) {
                ((FloatBuffer) buffer).put((float[]) valueEntry.getValue());
            } else if (buffer instanceof IntBuffer) {
                ((IntBuffer) buffer).put((int[]) valueEntry.getValue());
            } else if (buffer instanceof ShortBuffer) {
                ((ShortBuffer) buffer).put((short[]) valueEntry.getValue());
            } else if (buffer instanceof ByteBuffer) {
                ((ByteBuffer) buffer).put((byte[]) valueEntry.getValue());
            }
        }
    }
 
    public void addVertex(Vector3f position) {
        setPosition(position);
        addVertex();
    }
 
    public void addVertex(float x, float y, float z) {
        setPosition(x, y, z);
        addVertex();
    }
 
    public void addVertex(Vector3f position, ColorRGBA color) {
        setPosition(position);
        setColor(color);
        addVertex();
    }
 
    public void addVertex(Vector3f position, ColorRGBA color, Vector3f normal) {
        setPosition(position);
        setColor(color);
        setNormal(normal);
        addVertex();
    }
 
    public void setPosition(Vector3f vector) {
        setFloatValue(Type.Position, vector.x, vector.y, vector.z);
    }
 
    public void setPosition(float x, float y, float z) {
        setFloatValue(Type.Position, x, y, z);
    }
 
    public void setColor(ColorRGBA color) {
        setFloatValue(Type.Color, color.r, color.g, color.b, color.a);
    }
 
    public void setColor(float r, float g, float b, float a) {
        setFloatValue(Type.Color, r, g, b, a);
    }
 
    public void setNormal(Vector3f vector) {
        setFloatValue(Type.Normal, vector.x, vector.y, vector.z);
    }
 
    public void setNormal(float x, float y, float z) {
        setFloatValue(Type.Normal, x, y, z);
    }
 
    public void setFloatValue(Type type, float... value) {
        TypeMeta typeMeta = TypeMeta.getTypeMeta(type);
        if (typeMeta.getBufferType() != FloatBuffer.class) throw new IllegalArgumentException("This buffer does not take 'float' values!");
        if (typeMeta.getNumValues() != value.length) throw new IllegalArgumentException("This buffer needs " + typeMeta.getNumValues() + " values!");
        values.put(type, value);
    }
 
    public void setShortValue(Type type, short... value) {
        TypeMeta typeMeta = TypeMeta.getTypeMeta(type);
        if (typeMeta.getBufferType() != ShortBuffer.class) throw new IllegalArgumentException("This buffer does not take 'short' values!");
        if (typeMeta.getNumValues() != value.length) throw new IllegalArgumentException("This buffer needs " + typeMeta.getNumValues() + " values!");
        values.put(type, value);
    }
 
    public void setIntValue(Type type, int... value) {
        TypeMeta typeMeta = TypeMeta.getTypeMeta(type);
        if (typeMeta.getBufferType() != IntBuffer.class) throw new IllegalArgumentException("This buffer does not take 'int' values!");
        if (typeMeta.getNumValues() != value.length) throw new IllegalArgumentException("This buffer needs " + typeMeta.getNumValues() + " values!");
        values.put(type, value);
    }
 
    public void setByteValue(Type type, byte... value) {
        TypeMeta typeMeta = TypeMeta.getTypeMeta(type);
        if (typeMeta.getBufferType() != ByteBuffer.class) throw new IllegalArgumentException("This buffer does not take 'byte' values!");
        if (typeMeta.getNumValues() != value.length) throw new IllegalArgumentException("This buffer needs " + typeMeta.getNumValues() + " values!");
        values.put(type, value);
    }
 
    public void finish() {
        for (Entry<Type, Buffer> bufferEntry : buffers.entrySet()) {
            Type type = bufferEntry.getKey();
            Buffer buffer = bufferEntry.getValue();
            buffer.flip();
            TypeMeta typeMeta = TypeMeta.getTypeMeta(type);
            if (buffer instanceof FloatBuffer) {
                setBuffer(type, typeMeta.getNumValues(), (FloatBuffer) buffer);
            } else if (buffer instanceof IntBuffer) {
                setBuffer(type, typeMeta.getNumValues(), (IntBuffer) buffer);
            } else if (buffer instanceof ShortBuffer) {
                setBuffer(type, typeMeta.getNumValues(), (ShortBuffer) buffer);
            } else if (buffer instanceof ByteBuffer) {
                setBuffer(type, typeMeta.getNumValues(), (ByteBuffer) buffer);
            }
        }
        updateBound();
        updateCounts();
    }
 
    /**
     * This helper class specifies the meta data needed to create the respective buffers automatically.<br>
     * This whole functionality COULD be integrated into the {@link Type} class to reduce unnecessary overhead!
     *
     * @author Finomosec
     *
     */
    private static class TypeMeta {
 
        private static Map<Type, TypeMeta> mapping;
 
        public static TypeMeta getTypeMeta(Type type) {
            if (mapping == null) {
                mapping = new HashMap<Type, TypeMeta>();
                mapping.put(Type.Index, new TypeMeta(1, IntBuffer.class));
                mapping.put(Type.Position, new TypeMeta(3, FloatBuffer.class));
                mapping.put(Type.Normal, new TypeMeta(3, FloatBuffer.class));
                mapping.put(Type.Size, new TypeMeta(1, FloatBuffer.class));
                mapping.put(Type.Color, new TypeMeta(4, FloatBuffer.class));
                // TODO: Complete with other Types! OR Alternatively add this data and functionality in the Type Enum!!
            }
            return mapping.get(type);
        }
 
        private int numValues;
        private Class<? extends Buffer> bufferType;
 
        private TypeMeta(int numValues, Class<? extends Buffer> bufferType) {
            this.numValues = numValues;
            this.bufferType = bufferType;
        }
 
        public int getNumValues() {
            return numValues;
        }
 
        public Class<? extends Buffer> getBufferType() {
            return bufferType;
        }
 
        public Buffer createBuffer(int initialSize) {
            Buffer buffer = null;
            if (getBufferType() == FloatBuffer.class) buffer = BufferUtils.createFloatBuffer(initialSize * numValues);
            else if (getBufferType() == IntBuffer.class) buffer = BufferUtils.createIntBuffer(initialSize * numValues);
            else if (getBufferType() == ShortBuffer.class) buffer = BufferUtils.createShortBuffer(initialSize * numValues);
            else if (getBufferType() == ByteBuffer.class) buffer = BufferUtils.createByteBuffer(initialSize * numValues);
            return buffer;
        }
 
    }
 
}