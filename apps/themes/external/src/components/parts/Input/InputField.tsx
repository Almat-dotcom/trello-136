import { ChangeEvent, MouseEvent, useRef, useState } from "react";
import InputContainer from "./InputContainer";
import Visibility from "./visibility.svg";
import VisibilityOff from "./visibility_off.svg";

type InputFieldProps = {
    fieldName: string,
    label: string,
    type: string,
    value: string,
    error?: string,
    readOnly?: boolean,
    onChange: (event: ChangeEvent<HTMLInputElement>) => void
}

const InputField = ({ fieldName, label, type, value, error, readOnly, onChange }: InputFieldProps) => {
    const [ changed, setChanged ] = useState(false)
    const [ switched, setSwitched ] = useState(false)
    
    const inputRef = useRef<HTMLInputElement>(null)

    const onInputChanged = (event: ChangeEvent<HTMLInputElement>) => {
        setChanged(true)
        onChange(event)
    }

    const onShowClicked = (event: MouseEvent<HTMLButtonElement>) => {
        setSwitched(!switched)
    }

    return (
        <InputContainer onFocus={() => inputRef?.current?.focus()} name={fieldName} label={label} error={changed ? undefined : error}>
            <input 
                ref={inputRef}
                type={ type === "password" && switched ? "text" : type }
                id={fieldName} 
                name={fieldName}
                className={ ( !changed && error ? "border-red-600" : "border-gray-300" ) + " block px-2.5 py-1 pt-4 w-full text-sm text-gray-900 bg-transparent rounded-lg border-2 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-primary peer"}
                placeholder=" "
                readOnly={readOnly}
                value={value}
                onChange={onInputChanged}
            />
            {type === "password" ? (
                <div className="absolute right-0 z-30 inset-y-1 flex items-center px-4 ">
                    <button type="button" onClick={onShowClicked} className="z-30 material-icons">
                        <img className="h-6" alt="visibility" src={switched ? VisibilityOff : Visibility}/>
                    </button>
                </div>
            ) : null }
        </InputContainer>
    );
};

export default InputField;
