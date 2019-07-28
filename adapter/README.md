# KAU :adapter

Helpers dealing with recyclerviews and adapters

## KauAnimator

Abstract base that decouples the animations into three parts: `add`, `remove`, and `change`.
Each component extends `KauAnimatorAdd`, `KauAnimatorRemove`, or `KauAnimatorChange` respectively.
All the changes in the original animator are removed, so you have complete control over the transitions.
There are a couple base animators, such as fade scale and slide, which can be mix and matched and added to `KauAnimator`