import { KeyboardEvent, useState, useRef, useEffect } from "react";
import 'react-phone-number-input/style.css';
import InputContainer from "./InputContainer";
import { countries, Country } from "../../../lib/countries";

const customStyles = `
  .PhoneInput {
    position: relative;
    display: flex;
    align-items: center;
  }
  
  .PhoneInputCountry {
    display: none !important;
    visibility: hidden !important;
    opacity: 0 !important;
    width: 0 !important;
    height: 0 !important;
    overflow: hidden !important;
    position: absolute !important;
    left: -9999px !important;
    pointer-events: none !important;
  }
  
  .PhoneInputCountryIcon {
    display: none !important;
    visibility: hidden !important;
    opacity: 0 !important;
  }
  
  .PhoneInputCountrySelectArrow {
    display: none !important;
    visibility: hidden !important;
    opacity: 0 !important;
  }
  
  .PhoneInputCountrySelect {
    display: none !important;
    visibility: hidden !important;
    opacity: 0 !important;
  }
  
  .PhoneInputInput {
    flex: 1;
    padding: 8px 12px;
    border: 1px solid #d1d5db;
    border-radius: 4px;
    font-size: 14px;
    outline: none;
    transition: border-color 0.2s;
    border-left: 1px solid #d1d5db !important;
    height: 38px;
  }
  
  .PhoneInputInput:focus {
    border-color: #3b82f6;
  }
`;

type InputPhoneWithCountryProps = {
    fieldName: string;
    label: string;
    placeholder?: string;
    required?: boolean;
    value: string;
    error?: string;
    onChange: (value: string) => void;
    isNonResident: boolean;
};

const InputPhoneWithCountry = ({ 
    fieldName, 
    label, 
    placeholder, 
    required, 
    value, 
    error, 
    onChange, 
    isNonResident 
}: InputPhoneWithCountryProps) => {
    const [memError, setMemError] = useState<string | undefined>(undefined);
    const [selectedCountry, setSelectedCountry] = useState<Country>(countries.find(c => c.code === 'KZ')!);
    const [expanded, setExpanded] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setExpanded(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const getMaxDigitsForCountry = (country: Country): number => {
        const countryCodeLength = country.phoneCode.replace('+', '').length;
        
        if (countryCodeLength === 1) return 10; // USA, Canada (+1)
        if (countryCodeLength === 2) return 9;  // Most European countries (+33, +44, etc.)
        if (countryCodeLength === 3) return 9;  // Most other countries (+998, +996, etc.)
        
        const specialCases: { [key: string]: number } = {
            'KZ': 11, // Kazakhstan
            'RU': 10, // Russia
            'US': 10, // USA
            'GB': 10, // UK
            'DE': 12, // Germany
            'FR': 9,  // France
            'IT': 10, // Italy
            'ES': 9,  // Spain
            'CN': 11, // China
            'IN': 10, // India
            'JP': 10, // Japan
            'KR': 10, // South Korea
            'BR': 11, // Brazil
            'MX': 10, // Mexico
            'AR': 10, // Argentina
            'AU': 9,  // Australia
            'CA': 10, // Canada
            'TR': 10, // Turkey
            'UA': 9,  // Ukraine
            'BY': 9,  // Belarus
            'UZ': 9,  // Uzbekistan
            'KG': 9,  // Kyrgyzstan
            'TJ': 9,  // Tajikistan
            'TM': 8,  // Turkmenistan
            'AZ': 9,  // Azerbaijan
            'GE': 9,  // Georgia
            'AM': 8,  // Armenia
        };
        
        return specialCases[country.code] || 10; // Default to 10
    };

    const formatPhoneNumberWithSpaces = (input: string, country: Country): string => {
        const digitsOnly = input.replace(/\D/g, '');
        
        if (digitsOnly.length === 0) {
            return country.phoneCode;
        }
        
        const countryCodeDigits = country.phoneCode.replace('+', '');
        let digitsAfterCode = digitsOnly;
        
        if (digitsOnly.startsWith(countryCodeDigits)) {
            digitsAfterCode = digitsOnly.substring(countryCodeDigits.length);
        } else {
            digitsAfterCode = digitsOnly;
        }
        
        const maxDigits = getMaxDigitsForCountry(country);
        if (digitsAfterCode.length > maxDigits) {
            digitsAfterCode = digitsAfterCode.substring(0, maxDigits);
        }
        
        let formattedNumber = country.phoneCode;
        
        if (digitsAfterCode.length > 0) {
            const countryCodeLength = country.phoneCode.replace('+', '').length;
            
            if (countryCodeLength === 1) {
                if (digitsAfterCode.length <= 3) {
                    formattedNumber += ' ' + digitsAfterCode;
                } else if (digitsAfterCode.length <= 6) {
                    formattedNumber += ' ' + digitsAfterCode.substring(0, 3) + ' ' + digitsAfterCode.substring(3);
                } else {
                    formattedNumber += ' ' + digitsAfterCode.substring(0, 3) + ' ' + digitsAfterCode.substring(3, 6) + ' ' + digitsAfterCode.substring(6);
                }
            } else if (countryCodeLength === 2) {
                if (digitsAfterCode.length <= 4) {
                    formattedNumber += ' ' + digitsAfterCode;
                } else {
                    formattedNumber += ' ' + digitsAfterCode.substring(0, 4) + ' ' + digitsAfterCode.substring(4);
                }
            } else if (countryCodeLength === 3) {
                if (digitsAfterCode.length <= 2) {
                    formattedNumber += ' ' + digitsAfterCode;
                } else if (digitsAfterCode.length <= 5) {
                    formattedNumber += ' ' + digitsAfterCode.substring(0, 2) + ' ' + digitsAfterCode.substring(2);
                } else if (digitsAfterCode.length <= 7) {
                    formattedNumber += ' ' + digitsAfterCode.substring(0, 2) + ' ' + digitsAfterCode.substring(2, 5) + ' ' + digitsAfterCode.substring(5);
                } else {
                    formattedNumber += ' ' + digitsAfterCode.substring(0, 2) + ' ' + digitsAfterCode.substring(2, 5) + ' ' + digitsAfterCode.substring(5, 7) + ' ' + digitsAfterCode.substring(7);
                }
            } else {
                const chunks = [];
                for (let i = 0; i < digitsAfterCode.length; i += 3) {
                    chunks.push(digitsAfterCode.substring(i, i + 3));
                }
                formattedNumber += ' ' + chunks.join(' ');
            }
        }
        
        return formattedNumber;
    };

    const onInputChanged = (newValue: string | undefined) => {
        setMemError(error);
        const phoneValue = newValue || '';
        
        // Format the phone number with automatic spacing
        const formattedValue = formatPhoneNumberWithSpaces(phoneValue, selectedCountry);
        onChange(formattedValue);
        
        // Clear any validation errors since we're auto-formatting
        setMemError(undefined);
    };

    const onKeyPressed = (event: KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            event.preventDefault();
        }
        
        const currentDigits = (value || '').replace(/\D/g, '');
        const countryCodeDigits = selectedCountry.phoneCode.replace('+', '');
        const digitsAfterCode = currentDigits.startsWith(countryCodeDigits) 
            ? currentDigits.substring(countryCodeDigits.length) 
            : currentDigits;
        
        const maxDigits = getMaxDigitsForCountry(selectedCountry);
        
        if (['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'].includes(event.key)) {
            return;
        }
        
        if (digitsAfterCode.length >= maxDigits && /\d/.test(event.key)) {
            event.preventDefault();
        }
    };

    const handleCountrySelect = (country: Country) => {
        setSelectedCountry(country);
        setExpanded(false);
        
        let digitsAfterCode = '';
        
        if (value) {
            const digitsOnly = value.replace(/\D/g, '');
            
            const oldCountryCodeDigits = selectedCountry.phoneCode.replace('+', '');
            
            if (digitsOnly.startsWith(oldCountryCodeDigits)) {
                digitsAfterCode = digitsOnly.substring(oldCountryCodeDigits.length);
            } else {
                digitsAfterCode = digitsOnly;
            }
            
            const maxDigits = getMaxDigitsForCountry(country);
            if (digitsAfterCode.length > maxDigits) {
                digitsAfterCode = digitsAfterCode.substring(0, maxDigits);
            }
        }
        
        const newValue = formatPhoneNumberWithSpaces(country.phoneCode + digitsAfterCode, country);
        onChange(newValue);
    };

    if (isNonResident) {
        return (
            <InputContainer onFocus={() => {}} name={fieldName} required={required} label={label} error={error && memError === error ? undefined : error}>
                <style>{customStyles}</style>
                
                <div className="flex items-center gap-1">
                    <div className="relative flex-shrink-0" ref={dropdownRef}>
                        <button
                            type="button"
                            onClick={() => setExpanded(!expanded)}
                            className="flex items-center justify-center px-2 py-2 text-sm border border-gray-300 rounded-sm bg-white hover:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 w-12 h-[38px] z-10"
                        >
                            <span className="text-lg">{selectedCountry.flag}</span>
                        </button>
                        
                        {expanded && (
                            <div className="absolute z-50 w-64 mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto">
                                {countries.map((country) => (
                                    <button
                                        key={country.code}
                                        type="button"
                                        onClick={() => handleCountrySelect(country)}
                                        className="flex items-center w-full px-3 py-2 text-sm hover:bg-gray-100 focus:bg-gray-100 focus:outline-none"
                                    >
                                        <span className="mr-2 text-lg">{country.flag}</span>
                                        <span className="flex-1 text-left truncate">{country.name}</span>
                                        <span className="text-gray-500 flex-shrink-0">{country.phoneCode}</span>
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="flex-1">
                        <input
                            type="tel"
                            value={value}
                            onChange={(e) => onInputChanged(e.target.value)}
                            onKeyDown={onKeyPressed}
                            className={`block px-2.5 py-2 w-full border rounded-sm text-sm ${error && memError !== error ? 'border-red-500' : 'border-gray-400 focus:border-primary-focus'} appearance-none focus:outline-none text-black h-[38px]`}
                            placeholder={placeholder ? placeholder : " "}
                        />
                    </div>
                </div>
            </InputContainer>
        );
    }

    return (
        <InputContainer onFocus={() => {}} name={fieldName} required={required} label={label} error={error && memError === error ? undefined : error}>
            <style>{customStyles}</style>
            <input
                type="tel"
                value={value}
                onChange={(e) => onInputChanged(e.target.value)}
                onKeyDown={onKeyPressed}
                className={`block px-2.5 py-2 w-full border rounded-sm text-sm ${error && memError !== error ? 'border-red-500' : 'border-gray-400 focus:border-primary-focus'} appearance-none focus:outline-none text-black`}
                placeholder="+7 777 777 77 77"
            />
        </InputContainer>
    );
};

export default InputPhoneWithCountry; 