package org.asciidoctor.extension;

import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.ast.DocumentRuby;

public abstract class Preprocessor extends Processor {

    public Preprocessor() {
        this(new HashMap<String, Object>());
    }
    
    public Preprocessor(Map<String, Object> config) {
        super(config);
    }

    public abstract PreprocessorReader process(DocumentRuby document, PreprocessorReader reader);

}
