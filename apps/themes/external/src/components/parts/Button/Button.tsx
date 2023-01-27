
type ButtonProps = {
    children: any,
    type?: "submit" | "button" | "link",
    href?: string,
    severity: "primary" | "secondary",
    disabled?: boolean,
    onClick?: () => void
}

const Button = ({ children, type, href, severity, disabled, onClick }: ButtonProps) => {
    const btnType = type === "submit" ? "submit" : "button";
    return (
        <div className="mt-4">
            { type === "link" ?
                (
                    <a href={href} className={`block w-full px-4 py-1 tracking-wide transition-colors duration-200 transform rounded-sm no-underline ${severityStyles(severity)}`}>
                        {children}
                    </a>
                ) :
                (<button
                    type={btnType}
                    onClick={onClick ?? (() => { })}
                    className={`w-full px-4 py-1 tracking-wide transition-colors duration-200 transform rounded-sm disabled:bg-gray-700 focus:outline-none focus:ring ${severityStyles(severity)}`}
                    disabled={disabled}
                >
                    {children}
                </button>
                )
            }
        </div>
    );
}

const severityStyles = (severity: "primary" | "secondary") => 
    severity === "primary" 
        ? "bg-primary border border-primary hover:bg-primary-focus hover:ring-gray-800 hover:ring-opacity-50 focus:bg-primary-focus focus:ring-gray-800 focus:ring-opacity-50 text-white"
        : "bg-white border border-primary hover:border-primary-focus hover:ring-gray-800 hover:ring-opacity-50 focus:border-primary-focus focus:ring-gray-800 focus:ring-opacity-50 text-primary focus:text-primary-focus hover:text-primary-focus"

export default Button;
