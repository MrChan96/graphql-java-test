package priv.csh.graphql.demo;

import priv.csh.graphql.vo.User;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * 以编程方式作为Java代码定义模型，进行查询单个对象操作。
 * 输出结果：
 * query:{user(id:100){id,name}}
 * {data={user={id=100, name=张三100}}}
 */
public class GraphQLDemo {

    public static void main(String[] args) {

        /**
         * type User { #定义对象}
         */
        GraphQLObjectType userObjectType = newObject()
                .name("users")
                .field(newFieldDefinition().name("id").type(GraphQLLong))
                .field(newFieldDefinition().name("name").type(GraphQLString))
                .field(newFieldDefinition().name("age").type(GraphQLInt))
                .build();

        /**
         * user : User #指定对象以及参数类型
         */
        /**
         * 第一种：query:{user(id:100){id,name}}
         *       {data={user={id=1, name=张三}}}
         */
//        GraphQLFieldDefinition userFieldDefinition = newFieldDefinition()
//                .name("user")
//                .type(userObjectType)
//                .argument(newArgument().name("id").type(GraphQLLong).build())
//                .dataFetcher(new StaticDataFetcher(new User(1L, "张三", 20, null)))
//                .build();

        /**
         * 第二种：输出结果 ：{data={user={id=100, name=张三100}}}
         * 使用需要设置project structure  8-Lambdas type annotations etc
         */
        GraphQLFieldDefinition userFieldDefinition = newFieldDefinition()
                .name("user")
                .type(userObjectType)
                .argument(newArgument().name("id").type(GraphQLLong).build())
                .dataFetcher(environment -> {
                    Long id = environment.getArgument("id");
                    return new User(id,"张三"+id,20+id.intValue(),null);
                })
                .build();

        /**
         * type UserQuery { #定义查询的类型}
         */
        GraphQLObjectType userQueryObjectType = newObject()
                .name("UserQuery")
                .field(userFieldDefinition)
                .build();

        /**
         * schema { #定义查询 }
         */
        GraphQLSchema graphQLSchema = GraphQLSchema.newSchema().query(userQueryObjectType).build();

        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        String query = "{user(id:100){id,name}}";
        ExecutionResult result = graphQL.execute(query);

        System.out.println("query:" + query);
        //System.out.println(result.getErrors());
        //System.out.println(result.getData());

        System.out.println(result.toSpecification());  // 规范标准的的输出格式  {data={user={id=1, name=张三}}}

    }
}
