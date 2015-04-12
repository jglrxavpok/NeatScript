package org.jglr.ns;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jglrxavpok on 11/04/2015.
 */
public class NSPreprocessor {

    private static final String IF_DEF = "#ifdef";

    private static final String IF_NOT_DEF = "#ifndef";

    private static final String ENDIF = "#endif";

    private static final String DEFINE = "#def";
    private HashMapWithDefault<String, Boolean> definitionMap;

    public NSPreprocessor() {
        definitionMap = new HashMapWithDefault<>();
        definitionMap.setDefault(false);
    }

    public String preprocess(NSSourceFile sourceFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        String input = sourceFile.content();
        String[] lines = input.split("\n");
        Pattern pattern = Pattern.compile("( +)?(#[^ ]+)( +)?([^ \n\r]+)?");

        boolean skip = false;
        for(String line : lines) {
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String command = matcher.group(2);
                String argument = matcher.group(4);

                switch (command) {
                    case IF_DEF:
                        skip = !definitionMap.get(argument);
                        continue;

                    case IF_NOT_DEF:
                        skip = definitionMap.get(argument);
                        continue;

                    case ENDIF:
                        skip = false;
                        continue;

                    case DEFINE:
                        if(!skip) {
                            definitionMap.put(argument, true);
                        }
                        continue;

                    default:
                      throw new UnsupportedOperationException("Command " + command + " is unknown");
                }
            }

            if(!skip) {
                builder.append(line);
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
