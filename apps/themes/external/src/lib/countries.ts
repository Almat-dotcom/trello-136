import { countries as countriesList } from 'countries-list';
import { getCountryCallingCode } from 'libphonenumber-js';

export interface Country {
    code: string;
    name: string;
    phoneCode: string;
    flag: string;
    minDigits: number;
    maxDigits: number;
    mask: string;
}

export const countries: Country[] = Object.entries(countriesList)
    .map(([code, country]) => {
        try {
            const phoneCode = getCountryCallingCode(code as any);
            return {
                code: code.toUpperCase(),
                name: country.name,
                phoneCode: `+${phoneCode}`,
                flag: getCountryFlag(code.toUpperCase()),
                minDigits: 6,
                maxDigits: 11,
                mask: ""
            };
        } catch (error) {
            return null;
        }
    })
    .filter(Boolean)
    .sort((a, b) => {
        if (a?.code === 'KZ') return -1;
        if (b?.code === 'KZ') return 1;
        return a!.name.localeCompare(b!.name, 'ru');
    }) as Country[];

function getCountryFlag(countryCode: string): string {
    const codePoints = countryCode
        .toUpperCase()
        .split('')
        .map(char => 127397 + char.charCodeAt(0));
    return String.fromCodePoint(...codePoints);
} 