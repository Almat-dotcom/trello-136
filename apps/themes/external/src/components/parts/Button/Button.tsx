
type ButtonProps = {
    children: any,
    type?: "submit" | "button",
    severity: "primary",
    disabled?: boolean,
    onClick?: () => void
}

const Button = ({ children, type, severity, disabled, onClick }: ButtonProps) => (
    <div className="mt-6">
        <button
            type={type ?? "button"}
            onClick={onClick ?? (() => { })}
            className={`w-full px-4 py-4 tracking-wide text-white transition-colors duration-200 transform rounded-md disabled:bg-gray-700 focus:outline-none focus:ring ${severityStyles(severity)}`}
            disabled={disabled}
        >
            {children}
        </button>
    </div>
);

const severityStyles = (severity: "primary") => "bg-primary hover:bg-primary-focus hover:ring-gray-800 hover:ring-opacity-50 focus:bg-primary-focus focus:ring-gray-800 focus:ring-opacity-50"

export default Button;
