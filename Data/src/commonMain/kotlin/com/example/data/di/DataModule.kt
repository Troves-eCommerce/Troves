import com.example.data.network.ShopifyNetworkClient
import org.koin.dsl.module

val dataModule = module {
    single { ShopifyNetworkClient.httpClient }
}