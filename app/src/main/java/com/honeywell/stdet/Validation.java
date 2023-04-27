package com.honeywell.stdet;
import java.io.Serializable;

public class Validation implements Serializable {

    public VALIDATION getValidation() {
        return validation;
    }

    public void setValidation(VALIDATION validation) {
        this.validation = validation;
    }

    public String getValidationMessageValid() {
        return validationmessage_valid;
    }

    public void setValidationMessageValid(String validationmessage_valid) {
        this.validationmessage_valid = validationmessage_valid;
    }

    public String getValidationMessageWarning() {
        return validationmessage_warning;
    }

    public void setValidationMessageWarning(String validationmessage_warning) {
        this.validationmessage_warning = validationmessage_warning;
    }

    public String getValidationMessageError() {
        return validationmessage_error;
    }

    public String getValidationMessage() {
       if (validation== VALIDATION.WARNING || validation == VALIDATION.WARNING_DUPLICATE)
          return getValidationMessageWarning();
       else if (validation== VALIDATION.ERROR)
           return getValidationMessageError();
       else
           return  getValidationMessageValid();
    }

    public void setValidationMessageError(String validationmessage_error) {
        this.validationmessage_error = validationmessage_error;
    }

    public void addToValidationMessageError(String validationmessage_error, String delimiter) {
        this.validationmessage_error += validationmessage_error +  delimiter;
    }
    public void addToValidationMessageError(String validationmessage_error) {
        addToValidationMessageError(validationmessage_error,"");
    }

    public void addToValidationMessageWarning(String validationmessage_warning, String delimiter) {
        this.validationmessage_warning += validationmessage_warning +delimiter;
    }
    public void addToValidationMessageWarning(String validationmessage_warning) {
        addToValidationMessageWarning(validationmessage_warning,"");
    }

    public FOCUS getFocus() {
        return focus;
    }

    public void setFocus(FOCUS focus) {
        this.focus = focus;
    }

    public enum VALIDATION {
        VALID, WARNING, WARNING_DUPLICATE, ERROR;

        @Override
        public String toString() {
            return "VALIDATION{}";
        }

        public int value() {

            if (this.toString().equals("VALID")) return 0;
            if (this.toString().equals("WARNING")) return 1;
            if (this.toString().equals("WARNING_DUPLICATE")) return 2;

            else return 4;
        }
    }
    public enum FOCUS {
        COLLECTOR, LOCATION, READING, ELEVATION;

        @Override
        public String toString() {
            return "FOCUS{}";
        }
    }
    private  VALIDATION validation;
    private String validationmessage_valid;
    private String validationmessage_warning;
    private String validationmessage_error;

    private FOCUS focus ;

    public Validation(){
        validationmessage_valid = validationmessage_error = validationmessage_warning = "";
        validation = VALIDATION.VALID;
        setFocus(FOCUS.READING);
    }

    public boolean isError()
    {
        if (validation == VALIDATION.ERROR)
            return true;
        else return false;
    }
    public boolean isWarning()
    {
        if (validation == VALIDATION.WARNING)
            return true;
        else return false;
    }
    public boolean isWarningDuplicate()
    {
        if (validation == VALIDATION.WARNING_DUPLICATE)
            return true;
        else return false;
    }
    public boolean isValid()
    {
        if (validation == VALIDATION.VALID)
            return true;
        else return false;
    }

}
