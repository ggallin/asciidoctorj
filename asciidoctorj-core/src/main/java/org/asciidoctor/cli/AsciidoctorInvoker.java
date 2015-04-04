package org.asciidoctor.cli;

import com.beust.jcommander.JCommander;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.DirectoryWalker;
import org.asciidoctor.GlobDirectoryWalker;
import org.asciidoctor.Options;
import org.asciidoctor.internal.JRubyAsciidoctor;
import org.asciidoctor.internal.RubyHashUtil;
import org.asciidoctor.internal.RubyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AsciidoctorInvoker {

    public void invoke(String... parameters) {

        AsciidoctorCliOptions asciidoctorCliOptions = new AsciidoctorCliOptions();
        JCommander jCommander = new JCommander(asciidoctorCliOptions,
                parameters);

        if (asciidoctorCliOptions.isHelp() || parameters.length == 0) {
            jCommander.setProgramName("asciidoctor");
            jCommander.usage();
        } else {

            JRubyAsciidoctor asciidoctor = buildAsciidoctorJInstance(asciidoctorCliOptions);
            
            if (asciidoctorCliOptions.isVersion()) {
                System.out.println("Asciidoctor " + asciidoctor.asciidoctorVersion() + " [http://asciidoctor.org]");
                return;
            }
            
            List<File> inputFiles = getInputFiles(asciidoctorCliOptions);

            if (inputFiles.isEmpty()) {
                System.err.println("asciidoctor: FAILED: input file(s) '"
                        + asciidoctorCliOptions.getParameters()
                        + "' missing or cannot be read");
                throw new IllegalArgumentException(
                        "asciidoctor: FAILED: input file(s) '"
                                + asciidoctorCliOptions.getParameters()
                                + "' missing or cannot be read");
            }

            Options options = asciidoctorCliOptions.parse();
            
            if(asciidoctorCliOptions.isRequire()) {
                for (String require : asciidoctorCliOptions.getRequire()) {
                    RubyUtils.requireLibrary(asciidoctor.getRubyRuntime(), require);
                }
            }
            
            setVerboseLevel(asciidoctor, asciidoctorCliOptions);
            
            String output = renderInput(asciidoctor, options, inputFiles);

            if (asciidoctorCliOptions.isVerbose()) {

                Map<String, Object> optionsMap = options.map();
                Map<Object, Object> monitor = RubyHashUtil
                        .convertRubyHashMapToMap((Map<Object, Object>) optionsMap
                                .get(AsciidoctorCliOptions.MONITOR_OPTION_NAME));

                System.out.println(String.format(
                        "Time to read and parse source: %05.5f",
                        monitor.get("parse")));
                System.out.println(String.format(
                        "Time to render document: %05.5f",
                        monitor.get("render")));
                System.out.println(String.format(
                        "Total time to read, parse and render: %05.5f",
                        monitor.get("load_render")));

            }

            if (!"".equals(output.trim())) {
                System.out.println(output);
            }
        }
    }

    private void setVerboseLevel(JRubyAsciidoctor asciidoctor, AsciidoctorCliOptions asciidoctorCliOptions) {
        if(asciidoctorCliOptions.isVerbose()) {
            RubyUtils.setGlobalVariable(asciidoctor.getRubyRuntime(), "VERBOSE", "true");
        } else {
            if(asciidoctorCliOptions.isQuiet()) {
                RubyUtils.setGlobalVariable(asciidoctor.getRubyRuntime(), "VERBOSE", "nil");
            }
        }
    }

    private JRubyAsciidoctor buildAsciidoctorJInstance(AsciidoctorCliOptions asciidoctorCliOptions) {
        JRubyAsciidoctor asciidoctor;
        if(asciidoctorCliOptions.isLoadPaths()) {
         asciidoctor = JRubyAsciidoctor.create(asciidoctorCliOptions.getLoadPaths());   
        } else {
            asciidoctor = JRubyAsciidoctor.create();
        }
        return asciidoctor;
    }

    private String renderInput(Asciidoctor asciidoctor, Options options, List<File> inputFiles) {
        

        // jcommander bug makes this code not working.
        // if("-".equals(inputFile)) {
        // return asciidoctor.render(readInputFromStdIn(), options);
        // }

        StringBuilder output = new StringBuilder();

        for (File inputFile : inputFiles) {

            if (inputFile.canRead()) {

                String renderedFile = asciidoctor
                        .renderFile(inputFile, options);
                if (renderedFile != null) {
                    output.append(renderedFile).append(
                            System.getProperty("line.separator"));
                }
            } else {
                System.err.println("asciidoctor: FAILED: input file(s) '"
                        + inputFile.getAbsolutePath()
                        + "' missing or cannot be read");
                throw new IllegalArgumentException(
                        "asciidoctor: FAILED: input file(s) '"
                                + inputFile.getAbsolutePath()
                                + "' missing or cannot be read");
            }
        }

        return output.toString();
    }

    private String readInputFromStdIn() {
        Scanner in = new Scanner(System.in);
        String content = in.nextLine();
        in.close();

        return content;
    }

    private List<File> getInputFiles(AsciidoctorCliOptions asciidoctorCliOptions) {

        List<String> parameters = asciidoctorCliOptions.getParameters();

        if (parameters.isEmpty()) {
            System.err.println("asciidoctor: FAILED: input file missing");
            throw new IllegalArgumentException(
                    "asciidoctor: FAILED: input file missing");
        }

        if (parameters.contains("-")) {
            System.err
                    .println("asciidoctor:  FAILED: input file is required instead of an argument.");
            throw new IllegalArgumentException(
                    "asciidoctor:  FAILED: input file is required instead of an argument.");
        }

        List<File> filesToBeRendered = new ArrayList<File>();

        for (String globExpression : parameters) {
            DirectoryWalker globDirectoryWalker = new GlobDirectoryWalker(globExpression);
            filesToBeRendered.addAll(globDirectoryWalker.scan());
        }

        return filesToBeRendered;

    }

    public static void main(String args[]) {
        new AsciidoctorInvoker().invoke(args);
    }

}
