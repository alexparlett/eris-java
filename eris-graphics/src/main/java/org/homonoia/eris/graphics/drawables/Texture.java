package org.homonoia.eris.graphics.drawables;

import com.google.gson.JsonObject;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.Image;
import org.lwjgl.opengl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexparlett on 21/02/2016.
 */
public abstract class Texture extends Resource {

    private static final Map<String, Integer> wrapMap = new HashMap<>();

    static {
        wrapMap.put("REPEAT", GL11.GL_REPEAT);
        wrapMap.put("MIRRORED_REPEAT", GL14.GL_MIRRORED_REPEAT);
        wrapMap.put("CLAMP_TO_BORDER", GL12.GL_CLAMP_TO_EDGE);
        wrapMap.put("CLAMP_TO_EDGE", GL13.GL_CLAMP_TO_BORDER);
        wrapMap.put("CLAMP", GL11.GL_CLAMP);
    }

    protected boolean generateMipMaps = true;
    protected int uWrapMode = GL11.GL_REPEAT;
    protected int vWrapMode = GL11.GL_REPEAT;
    protected int wWrapMode = GL11.GL_REPEAT;
    protected Integer handle;

    public Texture(final Context context) {
        super(context);
    }

    public void setGenerateMipMaps(final boolean generateMipMaps) {
        this.generateMipMaps = generateMipMaps;
    }

    public void setUWrapMode(final int uWrapMode) {
        this.uWrapMode = uWrapMode;
    }

    public void setVWrapMode(final int vWrapMode) {
        this.vWrapMode = vWrapMode;
    }

    public void setWWrapMode(final int wWrapMode) {
        this.wWrapMode = wWrapMode;
    }

    protected int getFormat(Image image)
    {
        switch (image.getComponents())
        {
            case 1:
                return GL11.GL_RED;
            case 2:
                return GL30.GL_RG;
            case 3:
                return GL11.GL_RGB;
            case 4:
                return GL11.GL_RGBA;
            default:
                return GL11.GL_RGB;
        }
    }

    protected void parseParameters(final JsonObject element)
    {
        if (element.has("mipmaps"))
        {
            generateMipMaps = element.get("mipmaps").getAsBoolean();
        }

        if (element.has("wrap"))
        {
            JsonObject wrap = element.get("wrap").getAsJsonObject();
            uWrapMode = wrapMap.get(wrap.get("u").getAsString());
            vWrapMode = wrapMap.get(wrap.get("v").getAsString());
            wWrapMode = wrapMap.get(wrap.get("w").getAsString());
        }
    }

    protected void setParameters()
    {
        if (generateMipMaps) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, generateMipMaps ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, uWrapMode);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, vWrapMode);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_WRAP_R, wWrapMode);
    }
}
