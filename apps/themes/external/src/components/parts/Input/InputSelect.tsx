import { useState } from "react";
import InputContainer from "./InputContainer";
import ExpandLess from "./expand_less.svg";
import ExpandMore from "./expand_more.svg";

type SelectOption = {
    value: string,
    label: string
}

type InputSelectProps = {
    fieldName: string,
    label: string,
    options: SelectOption[],
    value?: string,
    error?: string,
    onValueChange: (item: SelectOption) => void
}

const InputSelect = ({ fieldName, label, options, value, error, onValueChange }: InputSelectProps) => {
    const [changed, setChanged] = useState(false)
    const currentOption = options.filter((it) => it.value === value)
    const [current, setCurrent] = useState(currentOption.length === 1 ? currentOption[0] : { value: "", label: " " })
    const [expanded, setExpanded] = useState(false)

    const onChoiceClicked = (option: SelectOption) => {
        setExpanded(false);
        if (current.value !== option.value) {
            setChanged(true);
            setCurrent(option);
            onValueChange(option);
        }
    }

    const Choice = ({ option, onClick }: { option: SelectOption, onClick: (option: SelectOption) => void }) => (
        <div className="cursor-pointer w-full border-gray-100 rounded-t border-b text-gray-600" onClick={() => onClick(option)}>
            <div
                className="flex w-full items-center p-2 pl-2 border-transparent bg-white border-l-2 relative hover:text-primary hover:border-primary"
                onClick={() => onClick(option)}
            >
                <div className="w-full items-center flex" onClick={() => onClick(option)}>
                    <span className="mx-2 leading-6" onClick={() => onClick(option)}>{option.label}</span>
                </div>
            </div>
        </div>
    )

    const choices = options.map((it, i) => <Choice key={i} option={it} onClick={onChoiceClicked} />)

    return (
        <InputContainer label={label} name={fieldName} error={changed ? undefined : error} onFocus={() => { }}>
            <div id="floating_outlined" className={(!changed && error ? "border-red-600" : "border-gray-300") + " flex w-full text-gray-900 rounded-lg border-2 dark:text-white dark:border-gray-600 dark:focus:border-primary focus:ring-0 focus:border-primary peer"}>
                <input
                    className="h-full w-full appearance-none peer block cursor-pointer px-2.5 py-1 pt-4 bg-transparent focus:outline-none"
                    id={fieldName + "_label"}
                    name={fieldName + "_label"}
                    value={current.label}
                    onChange={() => { }}
                    onClick={() => setExpanded(!expanded)}
                    readOnly
                />
                <input type="hidden" id={fieldName} name={fieldName} value={current.value} onChange={() => {}}/>
                <div>
                    <button
                        type="button"
                        className="cursor-pointer w-6 h-full flex items-center text-gray-400 outline-none focus:outline-none"
                        onClick={() => setExpanded(!expanded)}
                    >
                        <img alt="expand" src={expanded ? ExpandLess : ExpandMore} />
                    </button>
                </div>
                <div className={(expanded ? "absolute" : "hidden") + " shadow top-full z-40 w-full lef-0 rounded max-h-select overflow-y-auto"} >
                    <div className="flex flex-col w-full">
                        {choices}
                    </div>
                </div>
            </div>
        </InputContainer>
    );
};

export default InputSelect;
