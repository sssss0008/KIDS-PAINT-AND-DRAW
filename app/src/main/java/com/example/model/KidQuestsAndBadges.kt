package com.example.model

object KidQuestsAndBadges {

    val dailyPrompts = listOf(
        DrawingPrompt("Draw a friendly cat wearing cool sunglasses! 🕶️🐱", "🐱", "Animals"),
        DrawingPrompt("Draw an ice cream castle with candy flags! 🍦🏰", "🍦", "Fantasy"),
        DrawingPrompt("Draw a happy dinosaur on a speedy skateboard! 🛹🦕", "🦕", "Action"),
        DrawingPrompt("Draw a flying spaceship passing through a donut planet! 🍩🚀", "🚀", "Space"),
        DrawingPrompt("Draw a puppy dancing in the rain with a rainbow umbrella! ☔🐶", "🐶", "Cute"),
        DrawingPrompt("Draw a magical unicorn flying over fluffy pink clouds! 🌈🦄", "🦄", "Magic"),
        DrawingPrompt("Draw an underwater party with fish wearing silly hats! 🥳🐠", "🐠", "Ocean"),
        DrawingPrompt("Draw a superhero teddy bear flying into the sky! 🦸🧸", "🧸", "Hero"),
        DrawingPrompt("Draw a friendly monster eating delicious pizza! 🍕👾", "👾", "Funny"),
        DrawingPrompt("Draw a smiling sun wearing a flower crown! 🌸☀️", "☀️", "Nature"),
        DrawingPrompt("Draw your dream rocket ship heading to the cheese moon! 🧀🚀", "🌕", "Space"),
        DrawingPrompt("Draw a magical tree where cupcakes grow on branches! 🧁🌳", "🧁", "Fantasy")
    )

    fun getInitialBadges(): List<KidBadge> = listOf(
        KidBadge("first_art", "First Masterpiece! 🎨", "Save your very first artwork to the gallery", "🏆", false, 0, 1),
        KidBadge("rainbow_master", "Rainbow Wizard 🌈", "Draw 5 times with the Magic Rainbow brush", "🌈", false, 0, 5),
        KidBadge("sticker_star", "Sticker Mania ⭐", "Stamp 10 cute stickers on your canvas", "⭐", false, 0, 10),
        KidBadge("bubble_blower", "Bubble Champion 🫧", "Draw 5 paths with bubbly brush", "🫧", false, 0, 5),
        KidBadge("fireworks_hero", "Sparkle Sparkler 🎆", "Light up the canvas with Fireworks brush", "🎆", false, 0, 5),
        KidBadge("shape_genius", "Shape Master 📐", "Create 5 geometric shapes", "🔷", false, 0, 5),
        KidBadge("chalk_crafter", "Chalkboard Pro 🪵", "Draw on the Chalkboard paper", "🪵", false, 0, 1),
        KidBadge("movie_director", "Magic Movie Director 🎬", "Watch your drawing animate in Magic Replay", "🎬", false, 0, 1)
    )
}
