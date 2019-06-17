package T145.metaltransport.api.profiles;

public interface IFactory<T> {

	IProfile create(T param);
}
