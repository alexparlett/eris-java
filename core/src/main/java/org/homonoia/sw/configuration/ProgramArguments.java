package org.homonoia.sw.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 28/04/2018
 */
@AllArgsConstructor
@Getter
public class ProgramArguments {

    private CommandLine commandLine;

}
