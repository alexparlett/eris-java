package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.parsers.Vector2fParser;
import org.homonoia.eris.core.parsers.Vector3fParser;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.mesh.Face;
import org.homonoia.eris.resources.types.mesh.Vertex;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by alexparlett on 07/05/2016.
 */
public class Mesh extends Resource {

    public static final String OBJ_GEOMETRY_VERTEX = "v";
    public static final String OBJ_TEXTURE_COORDS = "vt";
    public static final String OBJ_NORMAL = "vn";
    public static final String OBJ_FACE = "f";
    public static final String OBJ_COMMENT = "#";
    public static final Pattern OBJ_FACE_POINT_PATTERN = Pattern.compile("(\\s)");
    public static final Pattern OBJ_FACE_PATTERN = Pattern.compile("(\\/)");

    private List<Vector3f> geometry = new ArrayList<>();
    private List<Vector2f> textureCoords = new ArrayList<>();
    private List<Vector3f> normals = new ArrayList<>();
    private List<Face> faces = new ArrayList<>();

    public Mesh(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);

        reset();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.startsWith(OBJ_COMMENT)) {
                    continue;
                } else if (line.startsWith(OBJ_TEXTURE_COORDS)) {
                    textureCoords.add(processTextureCoord(line));
                } else if (line.startsWith(OBJ_NORMAL)) {
                    normals.add(processNormal(line));
                } else if (line.startsWith(OBJ_GEOMETRY_VERTEX)) {
                    geometry.add(processGeometry(line));
                } else if (line.startsWith(OBJ_FACE)) {
                    faces.add(processFace(line));
                }
            }
        } catch (ParseException | IndexOutOfBoundsException ex) {
            throw new IOException("Failed to parse obj", ex);
        }
    }

    @Override
    public void save(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        geometry.clear();
        textureCoords.clear();
        normals.clear();
        faces.clear();
    }

    public List<Vector3f> getGeometry() {
        return geometry;
    }

    public void setGeometry(final List<Vector3f> geometry) {
        this.geometry = geometry;
    }

    public List<Vector2f> getTextureCoords() {
        return textureCoords;
    }

    public void setTextureCoords(final List<Vector2f> textureCoords) {
        this.textureCoords = textureCoords;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public void setNormals(final List<Vector3f> normals) {
        this.normals = normals;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void setFaces(final List<Face> faces) {
        this.faces = faces;
    }

    private Face processFace(String line) throws ParseException {
        String[] groups = OBJ_FACE_POINT_PATTERN.split(line.substring(OBJ_FACE.length() + 1));
        if (groups.length != 3) {
            throw new ParseException("Invalid Face, obj must be in triangle format", 0);
        }

        Face face = new Face();
        for (String group : groups) {
            int indicesCounts = countCharacterOccurence(group, "/");

            String[] indices = OBJ_FACE_PATTERN.split(group);
            if (indicesCounts == 0) {
                int geometryIndex = Integer.parseInt(indices[0]) - 1;

                Vector3f position = geometry.get(geometryIndex);

                Vertex vertex = Vertex.builder()
                        .position(position)
                        .texCoords(new Vector2f(0,0))
                        .build();

                face.addVertex(vertex);
             } else if (indicesCounts == 1) {
                int geometryIndex = Integer.parseInt(indices[0]) - 1;
                int textureCoordsIndex = Integer.parseInt(indices[1]) - 1;

                Vector3f position = geometry.get(geometryIndex);
                Vector2f texCoord = textureCoords.get(textureCoordsIndex);

                Vertex vertex = Vertex.builder()
                        .position(position)
                        .texCoords(texCoord)
                        .build();

                face.addVertex(vertex);
             } else if (indicesCounts == 2) {
                int geometryIndex = Integer.parseInt(indices[0]) - 1;
                int textureCoordsIndex = Integer.parseInt(indices[1]) - 1;
                int normalIndex = Integer.parseInt(indices[2]) - 1;

                Vector3f position = geometry.get(geometryIndex);
                Vector3f normal = normals.get(normalIndex);
                Vector2f texCoord = textureCoords.get(textureCoordsIndex);

                Vertex vertex = Vertex.builder()
                        .position(position)
                        .normal(normal)
                        .texCoords(texCoord)
                        .build();

                face.addVertex(vertex);
            }
        }

        if (normals.isEmpty()) {
            generateNormals(face);
        }

        return face;
    }

    private int countCharacterOccurence(final String string, final String character) {
        return string.length() - string.substring(0).replaceAll(character, "").length();
    }

    private Vector3f processNormal(String line) throws ParseException {
        return Vector3fParser.parse(line.substring(OBJ_NORMAL.length() + 1));
    }

    private Vector2f processTextureCoord(String line) throws ParseException {
        String substring = line.substring(OBJ_TEXTURE_COORDS.length() + 1);
        return Vector2fParser.parse(substring);
    }

    private Vector3f processGeometry(String line) throws ParseException {
        return Vector3fParser.parse(line.substring(OBJ_GEOMETRY_VERTEX.length() + 1));
    }

    private void generateNormals(Face face) {

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mesh mesh = (Mesh) o;

        if (geometry != null ? !geometry.equals(mesh.geometry) : mesh.geometry != null) return false;
        if (textureCoords != null ? !textureCoords.equals(mesh.textureCoords) : mesh.textureCoords != null)
            return false;
        if (normals != null ? !normals.equals(mesh.normals) : mesh.normals != null) return false;
        return faces != null ? faces.equals(mesh.faces) : mesh.faces == null;

    }

    @Override
    public int hashCode() {
        int result = geometry != null ? geometry.hashCode() : 0;
        result = 31 * result + (textureCoords != null ? textureCoords.hashCode() : 0);
        result = 31 * result + (normals != null ? normals.hashCode() : 0);
        result = 31 * result + (faces != null ? faces.hashCode() : 0);
        return result;
    }
}
