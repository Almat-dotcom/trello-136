import { ChangeEvent, KeyboardEvent, useRef, useState, LegacyRef, MutableRefObject } from "react";
import { InputMask as PrimeMask, InputMaskChangeParams } from "primereact/inputmask";
import InputContainer from "./InputContainer";

type InputMaskProps = {
    fieldName: string,
    label: string,
    value: string,
    mask: string,
    placeholder?: string,
    required?: boolean,
    error?: string,
    onChange: (value: string) => void,
    onEnter?: () => void
}

const InputMask = ({ fieldName, label, value, mask, placeholder, required, error, onChange, onEnter } : InputMaskProps) => {
    const [memError, setMemError] = useState<string | undefined>(undefined);

    const inputRef = useRef<PrimeMask>(null);

    const onInputChanged = (event: InputMaskChangeParams) => {
        setMemError(error)
        onChange(event.value)
    }

    const onKeyPressed = (event: KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter' && onEnter) {
            onEnter();
        }
    }

    return (
        <InputContainer  onFocus={() => {}} name={fieldName} required={required} label={label} error={error && memError === error ? undefined : error}>
            <PrimeMask
                ref={inputRef}
                id={fieldName}
                name={fieldName}
                className={`block px-2.5 py-2 w-full border rounded-sm text-sm ${error && memError !== error ? 'border-red-500' : 'border-gray-400 focus:border-primary-focus'} appearance-none focus:outline-none text-black`}
                mask={mask}
                placeholder={placeholder ? placeholder : " "}
                value={value}
                onChange={onInputChanged}
                onKeyDown={onKeyPressed}
            />
        </InputContainer>
    );
}

export default InputMask;
