Installation
------------

    mvn clean package
    
Usage
-----

### Encrypt

    $ ./run.sh encrypt 'Hello World!' arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4df5-ba4f-127dfb9a5031
     
### Decrypt

    $ ./run.sh decrypt CiA47hYvQqWFFGq3TLtzQO5ArcwDkjq69Q== arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4df5-ba4f-127dfb9a5031
