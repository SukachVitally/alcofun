import Container from "react-bootstrap/Container";
import ProviderItem from "./ProviderItem";
import { useTranslation } from "react-i18next";

const ProviderList = (props) => {
  const { t } = useTranslation();
  console.log(props);
  return (
    <>
      <h3>{t('product.providers')}</h3>
      <Container>
        {props.providers.map((item) => (
          <ProviderItem
            name={item.name}
            price={item.price}
            link={item.link}
            key={item.id}
            providerId={item.id}
          />
        ))}
      </Container>
    </>
  );
};

export default ProviderList;
