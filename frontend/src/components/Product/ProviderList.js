import Container from "react-bootstrap/Container";
import ProviderItem from "./ProviderItem";

const ProviderList = (props) => {
  console.log(props);
  return (
    <>
      <h3>Providers</h3>
      <Container>
        {props.providers.map((item) => (
          <ProviderItem
            name={item.providerType}
            price={item.price}
            link={item.link}
            key={item.providerId}
            providerId={item.providerId}
          />
        ))}
      </Container>
    </>
  );
};

export default ProviderList;
