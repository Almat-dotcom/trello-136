
type InputContainerProps = {
    children: any,
    name: string,
    label: string,
    error?: string,
    onFocus: () => void
}

const InputContainer = ({ children, name, label, error, onFocus }: InputContainerProps) => (
    <div className="flex flex-col w-100 h-18 mb-2">
        <div className="relative">
            {children}
            <label 
                htmlFor={name} 
                className="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-2 origin-[0] cursor-text bg-white dark:bg-gray-900 px-2 peer-focus:px-2 peer-focus:text-primary peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:-translate-y-1/2 peer-placeholder-shown:top-1/2 peer-focus:top-2 peer-focus:scale-75 peer-focus:-translate-y-4 left-1"
                onClick={() => onFocus()}
            >{label}</label>
        </div>
        <span className={ (error ? "text-red-600" : "text-white") + " pr-2 self-end text-sm text-left" }>{error ? error : "placeholder"}</span>
    </div>
);

export default InputContainer;
