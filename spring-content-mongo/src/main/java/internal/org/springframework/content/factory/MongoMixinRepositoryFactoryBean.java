package internal.org.springframework.content.factory;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class MongoMixinRepositoryFactoryBean <T extends Repository<S, ID>, S, ID extends Serializable> extends
	MongoRepositoryFactoryBean {

	private GridFsTemplate gridFs;

	@Autowired
	public void setGridFs(GridFsTemplate gridFs) {
		this.gridFs = gridFs;
	}

	@Override
	protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
		return new MongoMixinRepositoryFactory(operations, gridFs);
	}
}
