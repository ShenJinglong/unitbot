package xyz.sjinglong.unitbot;


import java.util.List;

public class TuLingResponse {
    private Intent intent;
    private List<Result> results;

    public Intent getIntent() {
        return intent;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public static class Intent {
        private int code;
        private String intentName;
        private String actionName;
        private Parameters parameters;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getIntentName() {
            return intentName;
        }

        public void setIntentName(String intentName) {
            this.intentName = intentName;
        }

        public String getActionName() {
            return actionName;
        }

        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

        public Parameters getParameters() {
            return parameters;
        }

        public void setParameters(Parameters parameters) {
            this.parameters = parameters;
        }

        public static class Parameters {
            private String nearby_place;

            public String getNearby_place() {
                return nearby_place;
            }

            public void setNearby_place(String nearby_place) {
                this.nearby_place = nearby_place;
            }
        }
    }

    public static class Result {
        private int groupType;
        private String resultType;
        private Values values;

        public int getGroupType() {
            return groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public Values getValues() {
            return values;
        }

        public void setValues(Values values) {
            this.values = values;
        }

        public static class Values {
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }
}
