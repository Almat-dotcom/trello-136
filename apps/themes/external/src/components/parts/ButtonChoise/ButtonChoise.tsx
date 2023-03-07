import Button from "../Button/Button";

type Choise = {
    name: string,
    onSelect: () => void
}

type ButtonChoiseProps = {
    choises: Choise[]
}

const ButtonChoise = ({ choises }: ButtonChoiseProps) => {
    const elements = choises.map((choise, i) => <Element key={i} name={choise.name} onSelect={choise.onSelect}/>)

    return (
        <div className="flex flex-wrap justify-around w-full">
            {elements}
        </div>
    );
}

const Element = ({ name, onSelect }: Choise) => (
    <div className="w-1/2 px-2">
        <Button severity="secondary-inline" type="button" onClick={onSelect}>{name}</Button>
    </div>
);

export default ButtonChoise;
