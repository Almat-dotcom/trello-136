
type InputContainerProps = {
    children: any,
    name: string,
    label: string,
    error?: string,
    onFocus: () => void
}

const InputContainer = ({ children, name, label, error, onFocus }: InputContainerProps) => (
    <div className="mb-2 ">
        <div className={`flex flex-col w-100 h-18 rounded-lg border-2 pt-2 pb-1 ${error ? "border-red-600" : "border-gray-300"}`}>
            <div className="relative">
                {children}
                <label
                    htmlFor={name}
                    style={{ top: "0.9rem" }}
                    className={`absolute text-sm ${error ? "text-red-600" : "text-gray-500"} duration-300 transform -translate-y-4 scale-75 origin-[0] cursor-text bg-white px-2 peer-focus:px-2 ${error ? "peer-focus:text-red-600" : "peer-focus:text-gray-500"} peer-placeholder-shown:scale-100 peer-placeholder-shown:-translate-y-1/2 peer-placeholder-shown:top-1/2 peer-focus:top-2 peer-focus:scale-75 peer-focus:-translate-y-4 left-1`}
                    onClick={() => onFocus()}
                >{label}</label>
            </div>
        </div>
        <span className={(error ? "text-red-600" : "text-white") + " pr-2 self-end text-sm text-left"}>{error ? error : "placeholder"}</span>
    </div>
);

export default InputContainer;
