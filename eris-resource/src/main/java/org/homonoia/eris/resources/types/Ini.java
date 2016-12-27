package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.Resource;
import org.homonoia.eris.resources.types.ini.IniException;
import org.homonoia.eris.resources.types.ini.IniSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 27/02/2016
 */
public class Ini extends Resource implements Iterable<Map.Entry<String, IniSection>> {

    private static final Pattern SECTION_PATTERN = Pattern.compile("^[\\[](.*)[\\]]");
    private static final Logger LOG = LoggerFactory.getLogger(Json.class);
    private static final String SECTION_START = "[";
    private static final String SECTION_END = "]";
    private static final String PROPERTY_SPLIT = "=";
    private static final String COMMENT_START = ";";

    private final Map<String, IniSection> sections = new LinkedHashMap<>();

    public Ini(final Context context) {
        super(context);
    }

    @Override
    public void load(final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        clear();

        try {
            String line;
            IniSection section = null;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.startsWith(COMMENT_START)) {
                    continue;
                } else if (SECTION_PATTERN.asPredicate().test(line)) {
                    Matcher matcher = SECTION_PATTERN.matcher(line);
                    if (matcher.find()) {
                        String sectionName = matcher.group(1);
                        section = add(sectionName);
                    }
                } else {
                    String[] split = line.split(PROPERTY_SPLIT);
                    if (split.length == 2 && section != null) {
                        section.set(split[0], split[1]);
                    } else {
                        throw new IOException(MessageFormat.format("Failed to load Ini {0}. Invalid Entry in {1}", getPath(), section));
                    }
                }
            }
        } catch (IniException ex) {
            throw new IOException(MessageFormat.format("Failed to load Ini {0}.", getPath()), ex);
        }

    }

    @Override
    public void save() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(getLocation().toFile())) {
            OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            for (Map.Entry<String, IniSection> section : sections.entrySet()) {
                osw.write(SECTION_START);
                osw.write(section.getKey());
                osw.write(SECTION_END);
                osw.write(System.lineSeparator());

                for (Map.Entry<String, String> property : section.getValue()) {
                    osw.write(property.getKey());
                    osw.write(PROPERTY_SPLIT);
                    osw.write(property.getValue());
                    osw.write(System.lineSeparator());
                }

                osw.write(System.lineSeparator());
            }
        }
    }

    @Override
    public Iterator<Map.Entry<String, IniSection>> iterator() {
        return sections.entrySet().iterator();
    }


    @Override
    public void forEach(final Consumer<? super Map.Entry<String, IniSection>> action) {
        Objects.requireNonNull(action);
        sections.entrySet().forEach(entry -> action.accept(entry));
    }


    @Override
    public Spliterator<Map.Entry<String, IniSection>> spliterator() {
        return sections.entrySet().spliterator();
    }

    public IniSection add(final String name) throws IniException {
        Objects.requireNonNull(name);
        if (StringUtils.isEmpty(name)) {
            throw new IniException("Cannot add a section with an empty name.");
        }
        IniSection iniSection = sections.get(name);
        if (iniSection != null) {
            return iniSection;
        }
        iniSection = new IniSection();
        sections.put(name, iniSection);
        return iniSection;
    }

    public Optional<IniSection> replace(final String name, final IniSection section) {
        Objects.requireNonNull(name);
        return Optional.ofNullable(sections.replace(name, section));
    }

    public Optional<IniSection> get(final String name) {
        Objects.requireNonNull(name);
        return Optional.ofNullable(sections.get(name));
    }

    public Optional<IniSection> remove(final String name) {
        Objects.requireNonNull(name);
        return Optional.ofNullable(sections.remove(name));
    }

    public void clear() {
        sections.clear();
    }

    public boolean contains(final String name) {
        Objects.requireNonNull(name);
        return sections.containsKey(name);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
