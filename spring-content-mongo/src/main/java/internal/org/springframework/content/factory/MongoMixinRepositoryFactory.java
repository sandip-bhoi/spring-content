package internal.org.springframework.content.factory;

import internal.org.springframework.content.repository.ContentEnabledSimpleMongoRepository;

import java.io.Serializable;

import org.springframework.content.common.repository.ContentRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

public class MongoMixinRepositoryFactory extends MongoRepositoryFactory {

	private MongoOperations mongoOps;
	private GridFsOperations gridFs;

	public MongoMixinRepositoryFactory(MongoOperations mongoOperations, GridFsOperations gridFs) {
		super(mongoOperations);
		this.mongoOps = mongoOperations;
		this.gridFs = gridFs;
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		if (isContentEnabledRepository(metadata.getRepositoryInterface()))
			return ContentEnabledSimpleMongoRepository.class;
		else
			return super.getRepositoryBaseClass(metadata);
	}

	@Override
	protected Object getTargetRepository(RepositoryMetadata metadata) {

		Class<?> repositoryInterface = metadata.getRepositoryInterface();
		MongoEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

		if (isContentEnabledRepository(repositoryInterface)) {
			return new ContentEnabledSimpleMongoRepository(entityInformation, mongoOps, this.gridFs);
		} else {
			return super.getTargetRepository(metadata);
		}
	}

	private boolean isContentEnabledRepository(Class<?> repositoryInterface) {
		return ContentRepository.class.isAssignableFrom(repositoryInterface);
	}


}
