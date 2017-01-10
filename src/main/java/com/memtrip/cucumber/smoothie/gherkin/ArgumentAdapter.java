package com.memtrip.cucumber.smoothie.gherkin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentAdapter {

    private ArgumentTypeMatcher argumentTypeMatcher;

    public ArgumentAdapter(ArgumentTypeMatcher argumentTypeMatcher) {
        this.argumentTypeMatcher = argumentTypeMatcher;
    }

    List<BehaviourArgument> getArgumentsFromPickleValue(String behaviourValue, String pickleValue) {

        List<String> argumentIndicators = getArgumentIndicators(behaviourValue);

        List<BehaviourArgument> arguments = getArguments(getBehaviourPieces(behaviourValue), pickleValue);

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).setKey(removeArgFormatting(argumentIndicators.get(i)));
        }

        return arguments;
    }

    private List<String> getArgumentIndicators(String value) {
        List<String> args = new ArrayList<>();

        Pattern p = Pattern.compile("<(.*?)>");
        Matcher matcher = p.matcher(value);

        while (matcher.find()) {
            args.add(matcher.group());
        }

        return args;
    }

    private String[] getBehaviourPieces(String behaviourValue) {

        final String ARG_MATCHER = "%a";

        String behaviourValueWithArgMatchers =
                behaviourValue.replaceAll("<(.*?)>", ARG_MATCHER).replaceAll("  ", " ");

        return behaviourValueWithArgMatchers.split(ARG_MATCHER);
    }

    private List<BehaviourArgument> getArguments(String[] behaviourPieces, String pickleValue) {

        List<BehaviourArgument> arguments = new ArrayList<>();

        Matcher matcher = argumentTypeMatcher.matchArgument(
                getPickleArgumentString(behaviourPieces, pickleValue));

        boolean result = matcher.find();

        if (result) {

            StringBuffer sb = new StringBuffer();

            do {
                String argument = matcher.group();

                BehaviourArgument behaviourArgument = new BehaviourArgument();
                behaviourArgument.setValue(removeLiteralQuotes(argument));
                behaviourArgument.setType(argumentTypeMatcher.getType(argument));

                arguments.add(behaviourArgument);

                matcher.appendReplacement(sb, "");
                result = matcher.find();
            } while (result);

            matcher.appendTail(sb);
        }

        return arguments;
    }

    private String getPickleArgumentString(String[] behaviourPieces, String pickleValue) {

        for (String behaviourPiece : behaviourPieces) {
            pickleValue = pickleValue.replace(behaviourPiece, " ");
        }

        return pickleValue;
    }

    private String removeLiteralQuotes(String literal) {
        return literal.replace("\"","").replace("\'","");
    }

    private String removeArgFormatting(String key) {
        return key.replace("<", "").replace(">", "");
    }
}