import { useRef, useState } from "react";

export const useTotpInput = (length = 6) => {
    const [otp, setOtp] = useState<string[]>(new Array(length).fill(""));
    const inputsRef = useRef<HTMLInputElement[]>([]);

    const handleChange = (value: string, index: number) => {
        const lastChar = value.slice(-1);
        const newOtp = [...otp];
        newOtp[index] = lastChar;
        setOtp(newOtp);

        if (lastChar && index < length - 1) {
            inputsRef.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === "Backspace" && !otp[index] && index > 0) {
            inputsRef.current[index - 1]?.focus();
        }
    };

    return { otp, setOtp, inputsRef, handleChange, handleKeyDown };
};