import React from "react";

interface TotpInputProps {
    otp: string[];
    inputsRef: React.MutableRefObject<HTMLInputElement[]>;
    handleChange: (value: string, index: number) => void;
    handleKeyDown: (e: React.KeyboardEvent<HTMLInputElement>, index: number) => void;
    containerClassName?: string; // Новый пропс для кастомизации стилей
    inputClassName?: string;     // Пропс для стилей инпутов
}

const TotpInput: React.FC<TotpInputProps> = ({
    otp,
    inputsRef,
    handleChange,
    handleKeyDown,
    containerClassName = "",
    inputClassName = "",
}) => {
    return (
        <div className={`grid grid-cols-6 gap-2 w-full max-w-sm mx-auto ${containerClassName}`}>
            {otp.map((digit, index) => (
                <input
                    key={index}
                    ref={(el) => (inputsRef.current[index] = el!)}
                    type="tel"
                    inputMode="numeric"
                    pattern="[0-9]*"
                    maxLength={1}
                    value={digit}
                    onChange={(e) => handleChange(e.target.value, index)}
                    onKeyDown={(e) => handleKeyDown(e, index)}
                    className={`
                        w-full 
                        aspect-square 
                        text-center text-xl font-semibold
                        border border-gray-300 
                        rounded-md 
                        focus:outline-none focus:ring-2 focus:ring-blue-500 
                        transition-all shadow-sm
                        ${inputClassName}
                    `}
                />
            ))}
        </div>
    );
};

export default TotpInput;

