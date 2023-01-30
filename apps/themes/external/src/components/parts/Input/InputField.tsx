import { ChangeEvent, MouseEvent, useRef, useState, KeyboardEvent } from "react";
import InputContainer from "./InputContainer";
import Visibility from "./visibility.svg";
import VisibilityOff from "./visibility_off.svg";

type InputFieldProps = {
    fieldName: string,
    label: string,
    type: string,
    value: string,
    placeholder?: string,
    required?: boolean,
    disabled?: boolean,
    error?: string,
    readOnly?: boolean,
    maxLength?: number,
    onChange: (event: ChangeEvent<HTMLInputElement>) => void,
    onEnter?: () => void
}

const InputField = ({ fieldName, label, type, value, placeholder, required, disabled, error, readOnly, maxLength, onChange, onEnter }: InputFieldProps) => {
    const [memError, setMemError] = useState<string | undefined>(undefined);
    const [switched, setSwitched] = useState(false);

    const inputRef = useRef<HTMLInputElement>(null);

    const onInputChanged = (event: ChangeEvent<HTMLInputElement>) => {
        setMemError(error);
        onChange(event);
    }

    const onShowClicked = (event: MouseEvent<HTMLButtonElement>) => {
        setSwitched(!switched);
    }

    const onKeyPressed = (event: KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter' && onEnter) {
            onEnter();
        }
    }

    return (
        <InputContainer onFocus={() => inputRef?.current?.focus()} name={fieldName} required={required} label={label} error={error && memError === error ? undefined : error}>
            <input
                ref={inputRef}
                type={type === "password" && switched ? "text" : type}
                id={fieldName}
                name={fieldName}
                className={`block px-2.5 py-2 w-full border rounded-sm text-sm ${error && memError !== error ? 'border-red-500' : 'border-gray-400 focus:border-primary-focus'} appearance-none focus:outline-none text-black ${disabled ? "cursor-not-allowed" : "cursor-text"}`}
                placeholder={placeholder ? placeholder : " "}
                readOnly={readOnly}
                maxLength={maxLength}
                value={value}
                disabled={disabled}
                onChange={onInputChanged}
                onKeyDown={onKeyPressed}
            />
            {type === "password" ? (
                <div className="absolute right-0 z-30 inset-y-1 flex items-center px-4 ">
                    <button type="button" onClick={onShowClicked} className="z-30 material-icons">
                        <img className="h-6 " alt="visibility" src={switched ? Visibility : VisibilityOff} />
                    </button>
                </div>
            ) : null}
        </InputContainer>
    );
};

export default InputField;
