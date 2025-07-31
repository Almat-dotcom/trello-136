package kz.kacd.sso.external.model.profile.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Optional;


public class PhoneNumberValidator {
    
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public static PhoneNumberValidationResult validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return PhoneNumberValidationResult.empty();
        }
        
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, null);
            
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                return PhoneNumberValidationResult.invalid("Invalid phone number format");
            }
            
            if (!phoneUtil.isPossibleNumber(parsedNumber)) {
                return PhoneNumberValidationResult.invalid("Phone number is not possible");
            }
            
            PhoneNumberUtil.PhoneNumberType numberType = phoneUtil.getNumberType(parsedNumber);
            
            String nationalNumber = String.valueOf(parsedNumber.getNationalNumber());
            if (nationalNumber.length() < 6 || nationalNumber.length() > 15) {
                return PhoneNumberValidationResult.invalid("Phone number length is invalid");
            }
            
            return PhoneNumberValidationResult.valid(parsedNumber, numberType);
            
        } catch (NumberParseException e) {
            return PhoneNumberValidationResult.invalid("Invalid phone number format: " + e.getMessage());
        } catch (Exception e) {
            return PhoneNumberValidationResult.invalid("Unexpected error during validation: " + e.getMessage());
        }
    }

    public static Optional<String> formatPhoneNumber(String phoneNumber) {
        PhoneNumberValidationResult result = validatePhoneNumber(phoneNumber);
        if (result.isValid()) {
            return Optional.of(phoneUtil.format(result.getPhoneNumber(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
        }
        return Optional.empty();
    }

    public static Optional<String> getCountryCode(String phoneNumber) {
        PhoneNumberValidationResult result = validatePhoneNumber(phoneNumber);
        if (result.isValid()) {
            return Optional.of("+" + result.getPhoneNumber().getCountryCode());
        }
        return Optional.empty();
    }

    public static Optional<String> getNationalNumber(String phoneNumber) {
        PhoneNumberValidationResult result = validatePhoneNumber(phoneNumber);
        if (result.isValid()) {
            return Optional.of(String.valueOf(result.getPhoneNumber().getNationalNumber()));
        }
        return Optional.empty();
    }

    public static boolean isMobileNumber(String phoneNumber) {
        PhoneNumberValidationResult result = validatePhoneNumber(phoneNumber);
        if (result.isValid()) {
            return result.getNumberType() == PhoneNumberUtil.PhoneNumberType.MOBILE;
        }
        return false;
    }

    public static class PhoneNumberValidationResult {
        private final boolean valid;
        private final Phonenumber.PhoneNumber phoneNumber;
        private final PhoneNumberUtil.PhoneNumberType numberType;
        private final String errorMessage;
        
        private PhoneNumberValidationResult(boolean valid, Phonenumber.PhoneNumber phoneNumber, 
                                         PhoneNumberUtil.PhoneNumberType numberType, String errorMessage) {
            this.valid = valid;
            this.phoneNumber = phoneNumber;
            this.numberType = numberType;
            this.errorMessage = errorMessage;
        }
        
        public static PhoneNumberValidationResult valid(Phonenumber.PhoneNumber phoneNumber, 
                                                      PhoneNumberUtil.PhoneNumberType numberType) {
            return new PhoneNumberValidationResult(true, phoneNumber, numberType, null);
        }
        
        public static PhoneNumberValidationResult invalid(String errorMessage) {
            return new PhoneNumberValidationResult(false, null, null, errorMessage);
        }
        
        public static PhoneNumberValidationResult empty() {
            return new PhoneNumberValidationResult(false, null, null, "Phone number is empty");
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public Phonenumber.PhoneNumber getPhoneNumber() {
            return phoneNumber;
        }
        
        public PhoneNumberUtil.PhoneNumberType getNumberType() {
            return numberType;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
} 