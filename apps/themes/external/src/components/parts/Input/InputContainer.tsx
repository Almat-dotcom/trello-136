
type InputContainerProps = {
    children: any,
    name: string,
    label: string,
    required?: boolean,
    error?: string,
    onFocus: () => void
}

const InputContainer = ({ children, name, label, required, error, onFocus }: InputContainerProps) => (
    <div>
        <div className="flex flex-col">
            <label
                htmlFor={name}
                className="text-dark-text"
                onClick={() => onFocus()}
            >{required ? <span className="mr-2 text-red-600">*</span> : null}{label}</label>
            <div className="relative mt-2">
                {children}
            </div>
        </div>
        <span className={(error ? "text-red-600" : "text-white") + " pr-2 self-end text-sm text-left"}>{error ? error : "placeholder"}</span>
    </div>
);

export default InputContainer;
