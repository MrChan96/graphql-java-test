package priv.csh.graphql.demo;

import priv.csh.graphql.vo.Card;
import priv.csh.graphql.vo.User;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * 使用SDL（graphql dsl）定义模式，查询对象嵌套的操作。
 * 输出结果：
 * user:priv.csh.graphql.vo.User@4c1d9d4b
 * query:{users(id:100){id,name,age,card{cardNumber}}}
 * {data={users={id=100, name=张三:100, age=120, card={cardNumber=123456789}}}}
 */
public class GraphQLSDLDemo {

    public static void main(String[] args) throws IOException {

        // 读取GraphQL文件，进行解析
        String fileName = "user.graphqls";
        String fileContent = IOUtils.toString(GraphQLSDLDemo.class.getClassLoader().getResource(fileName), "UTF-8");
        // 返回TypeDefinitionRegistry对象
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(fileContent);


        // 解决的是数据的查询
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("UserQuery", builder ->
                        builder.dataFetcher("users", environment -> {
                            Long id = environment.getArgument("id");
                            Card card = new Card("123456789", id);
                            User user = new User(id, "张三:" + id, 20 + id.intValue(), card);
                            System.out.println("user:"+user);
                            return user;
                        })
                )
                .build();


        // 生成Schema
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);


        // 根据Schema对象生成GraphQL对象
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        // 查询
        String query = "{users(id:100){id,name,age,card{cardNumber}}}";
        ExecutionResult result = graphQL.execute(query);

        System.out.println("query:" + query);
        System.out.println(result.toSpecification());

    }
}
