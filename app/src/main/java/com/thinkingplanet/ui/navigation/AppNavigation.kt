package com.thinkingplanet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.thinkingplanet.ui.chapter.ChapterScreen
import com.thinkingplanet.ui.honor.HonorScreen
import com.thinkingplanet.ui.question.QuestionScreen
import com.thinkingplanet.ui.result.ResultScreen
import com.thinkingplanet.ui.worldmap.WorldMapScreen

sealed class Screen(val route: String) {
    object WorldMap : Screen("worldmap")
    object Chapter  : Screen("chapter/{packId}") {
        fun go(packId: String) = "chapter/$packId"
    }
    object Question : Screen("question/{packId}/{dimension}/{levelId}") {
        fun go(packId: String, dimension: String, levelId: String) =
            "question/$packId/$dimension/$levelId"
    }
    object Result : Screen("result/{stars}") {
        fun go(stars: Int) = "result/$stars"
    }
    object Honor : Screen("honor")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.WorldMap.route) {
        composable(Screen.WorldMap.route) {
            WorldMapScreen(
                onChapterClick = { packId -> navController.navigate(Screen.Chapter.go(packId)) },
                onHonorClick   = { navController.navigate(Screen.Honor.route) }
            )
        }
        composable(
            Screen.Chapter.route,
            listOf(navArgument("packId") { type = NavType.StringType })
        ) { back ->
            val packId = back.arguments!!.getString("packId")!!
            ChapterScreen(
                packId = packId,
                onLevelClick = { dim, levelId -> navController.navigate(Screen.Question.go(packId, dim, levelId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Question.route,
            listOf(
                navArgument("packId")    { type = NavType.StringType },
                navArgument("dimension") { type = NavType.StringType },
                navArgument("levelId")   { type = NavType.StringType }
            )
        ) { back ->
            val args = back.arguments!!
            QuestionScreen(
                packId    = args.getString("packId")!!,
                dimension = args.getString("dimension")!!,
                levelId   = args.getString("levelId")!!,
                onFinish  = { stars ->
                    navController.navigate(Screen.Result.go(stars)) {
                        popUpTo(Screen.WorldMap.route)
                    }
                }
            )
        }
        composable(
            Screen.Result.route,
            listOf(navArgument("stars") { type = NavType.IntType })
        ) { back ->
            ResultScreen(
                stars  = back.arguments!!.getInt("stars"),
                onDone = { navController.popBackStack(Screen.WorldMap.route, false) }
            )
        }
        composable(Screen.Honor.route) {
            HonorScreen(onBack = { navController.popBackStack() })
        }
    }
}
