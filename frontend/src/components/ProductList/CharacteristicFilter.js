import CharacteristicFilterBlock from "./CharacteristicFilterBlock.js";

const CharacteristicFilter = (props) => {
  return (
    <>
      {props.types.map((type) => (
        <CharacteristicFilterBlock
          key={type.id}
          id={type.id}
          name={type.name}
          uom={type.uom}
          onChange={props.onChange}
          enabledCharacteristics={props.enabledCharacteristics}
        />
      ))}
    </>
  );
};

export default CharacteristicFilter;
