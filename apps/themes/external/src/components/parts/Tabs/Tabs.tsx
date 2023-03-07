import { useState } from "react";

type Element = {
    id: string,
    label: string,
    icon: any
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

    const children = elements.map((el) => <TabsElement key={el.id} id={el.id} label={el.label} icon={el.icon} choised={choised === el.id} onClick={onButtonClicked}/>)

    return (
        <div className="relative flex items-center mb-6 text-sm">
            <div className="relative flex items-center w-full p-1">
                {children}
            </div>
        </div>
    );
};

type TabsElementProps = {
    id: string,
    label: string,
    icon: any,
    choised: boolean,
    onClick: (id: string) => void
}

const TabsElement = ({ id, label, icon, choised, onClick }: TabsElementProps) => (
    <div className={ `flex justify-center mr-6 py-1.5 px-1 border-b-2 border-slate-300 text-slate-400 ${ choised ? "border-slate-900 text-primary-focus" : "" } hover:text-primary` }>
        <button type="button" onClick={() => onClick(id)}>
            {icon ? <img className="inline-block mr-2" alt="ico" src={icon}/> : null}{label}
        </button>
    </div>
)

export default Tabs;
