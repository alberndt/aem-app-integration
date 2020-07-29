package com.alexanderberndt.appintegration.html;

public class HtmlSnippetExtractor  {

    private static final String QUERY_PARAM = "query";

////    @Override
//    public IntegrationStepResult<?> validateConfiguration(ImmutableConfiguration configuration) {
//
//        ConfigValidator validator = new ConfigValidator(configuration);
//
//        validator.validateParam(QUERY_PARAM, String.class)
//                .required()
//                .nonBlank()
//                .validate(query -> {
//                    try {
//                        Selector.select(query, new Document("http://example.com"));
//                        return null;
//                    } catch (Selector.SelectorParseException e) {
//                        return e.getMessage();
//                    }
//                });
//
//        return validator.getResult();
//    }
//
//    public IntegrationStepResult<String> extractHtmlSnippet(ImmutableConfiguration configuration, Document htmlDoc) {
//        IntegrationStepResult<String> result = new IntegrationStepResult<>();
//        result.setResult("Hello");
//        return result;
//    }
//
}
