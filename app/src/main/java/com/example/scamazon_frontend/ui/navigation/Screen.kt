package com.example.scamazon_frontend.ui.navigation

/**
 * Navigation Routes - Lafyuu E-commerce App
 * Defines all screen routes in the application
 */
sealed class Screen(val route: String) {

    // ==========================================
    // AUTH SCREENS
    // ==========================================
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    // ==========================================
    // MAIN SCREENS (Bottom Navigation)
    // ==========================================
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Cart : Screen("cart")
    object Offer : Screen("offer")
    object Account : Screen("account")

    // ==========================================
    // PRODUCT SCREENS
    // ==========================================
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    object ProductList : Screen("product_list/{categoryId}") {
        fun createRoute(categoryId: String) = "product_list/$categoryId"
    }

    object Search : Screen("search")
    object SearchResult : Screen("search_result/{query}") {
        fun createRoute(query: String) = "search_result/$query"
    }

    // ==========================================
    // CART & CHECKOUT SCREENS
    // ==========================================
    object Checkout : Screen("checkout")
    object ShippingAddress : Screen("shipping_address")
    object PaymentMethod : Screen("payment_method")
    object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }

    // ==========================================
    // ACCOUNT SCREENS
    // ==========================================
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object OrderHistory : Screen("order_history")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object Wishlist : Screen("wishlist")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")

    // ==========================================
    // OTHER SCREENS
    // ==========================================
    object Map : Screen("map")
    object Chat : Screen("chat")
    object Review : Screen("review/{productId}") {
        fun createRoute(productId: String) = "review/$productId"
    }

    // ==========================================
    // ADMIN SCREENS
    // ==========================================
    object AdminDashboard : Screen("admin_dashboard")
    object AdminProducts : Screen("admin_products")
    object AdminProductAdd : Screen("admin_product_add")
    object AdminProductEdit : Screen("admin_product_edit/{productId}") {
        fun createRoute(productId: Int) = "admin_product_edit/$productId"
    }
    object AdminCategories : Screen("admin_categories")
    object AdminCategoryAdd : Screen("admin_category_add")
    object AdminCategoryEdit : Screen("admin_category_edit/{categoryId}") {
        fun createRoute(categoryId: Int) = "admin_category_edit/$categoryId"
    }
    object AdminBrandAdd : Screen("admin_brand_add")
    object AdminBrandEdit : Screen("admin_brand_edit/{brandId}") {
        fun createRoute(brandId: Int) = "admin_brand_edit/$brandId"
    }
    object AdminAccount : Screen("admin_account")
}

/**
 * Navigation Arguments
 */
object NavArgs {
    const val PRODUCT_ID = "productId"
    const val CATEGORY_ID = "categoryId"
    const val ORDER_ID = "orderId"
    const val QUERY = "query"
}
