# Twitter-clone
## Task 1
docker run --name my-redis -v (-p 6379:6379) redis-data:/data -d redis:alpine  
docker exec -it my-redis redis-cli  

## Task 2
Implement the tiny twitter clone, using only Redis as a data store.  
Look for the two classes PostManagementImpl and UserManagementImpl in this repo.  
  
## Task 3
In a readme, write a short explanation of your redis data model. It should be clear enough for a developer to be able to implement the same thing.


### Redis data model

```
User:username
  lastname: string
  birthday: string
  passwordHash: string
  numFollowers: string
  numFollowing: string
  
  following: set of usernames
  followed: set of usernames
  
  tweets: sorted set of tweets with milliseconds as score and message as member
```
