import Container from "react-bootstrap/Container";
import CharacteristicItem from "./CharacteristicItem";

const CharacteristicList = (props) => {
  return (
    <>
      <h3>Characteristics</h3>
      <Container>
        {props.characteristics.map((item) => (
          <CharacteristicItem name={item.type} value={item.value} key={item.id} id={item.id}/>
        ))}
      </Container>
    </>
  );
};

export default CharacteristicList;
