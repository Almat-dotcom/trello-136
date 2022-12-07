import { useState } from "react";

type Element = {
    id: string,
    label: string
}

type TabsProps = {
    elements: Element[],
    activeId?: string,
    onChange: (id: string) => void
}

const Tabs = ({ elements, activeId, onChange }: TabsProps) => {
    const [choised, setChoised] = useState(activeId ?? elements[0].id)

    const onButtonClicked = (id: string) => {
        if (id !== choised) {
            setChoised(id);
            onChange(id);
        }
    }

    const children = elements.map((el) => <TabsElement key={el.id} id={el.id} label={el.label} choised={choised === el.id} onClick={onButtonClicked}/>)

    return (
        <div className="mx-8 mb-3 shadow rounded-full flex relative items-center text-sm bg-gray-200">
            <div className="w-full flex p-1 relative items-center">
                {children}
            </div>
        </div>
    );
};

type TabsElementProps = {
    id: string,
    label: string,
    choised: boolean,
    onClick: (id: string) => void
}

const TabsElement = ({ id, label, choised, onClick }: TabsElementProps) => (
    <div className={ `w-full flex justify-center rounded-full ${ choised ? "bg-white" : "" } hover:text-primary` }>
        <button type="button" onClick={() => onClick(id)}>{label}</button>
    </div>
)

export default Tabs;
