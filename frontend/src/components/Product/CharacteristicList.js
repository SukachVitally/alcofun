import Container from "react-bootstrap/Container";
import CharacteristicItem from "./CharacteristicItem";
import { useTranslation } from "react-i18next";

const CharacteristicList = (props) => {
  const { t } = useTranslation();
  return (
    <>
      <h3>{t('product.characteristics')}</h3>
      <Container>
        {props.characteristics.map((item) => (
          <CharacteristicItem
            name={item.name}
            value={item.value}
            key={item.id}
            id={item.id}
          />
        ))}
      </Container>
    </>
  );
};

export default CharacteristicList;
