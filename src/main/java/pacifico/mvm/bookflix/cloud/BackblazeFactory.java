package pacifico.mvm.bookflix.cloud;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;

public class BackblazeFactory { 
	
	private final B2StorageClient b2Client;

    public BackblazeFactory(String keyId, String key, String agent) {
        this.b2Client = B2StorageClientFactory.createDefaultFactory().create(keyId, key, agent);
    }
	
	public Backblaze create(String bucketName) throws B2Exception {  
		B2Bucket bucket = b2Client.getBucketOrNullByName(bucketName); 
		if (bucket == null) throw new IllegalStateException("Bucket not found: " + bucketName); 
        return new Backblaze(b2Client, bucket);
	} 
	
}
