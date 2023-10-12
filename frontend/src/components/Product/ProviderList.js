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
