package net.codingstuffs.abilene.model.decision_making.calculators

import net.codingstuffs.abilene.model.decision_making.Models.{DecisionMakingModel, EgalitarianRoundup, SelfishRoundup, SimpleSociotropyAutonomy, SocialImpactNSL, WeightedRoundup, WeightedSociotropyAutonomy}
import net.codingstuffs.abilene.model.decision_making.generators.AgentParamGenerator.DecisionParams

object ModelParamAdjuster {

  def adjust(implicit model:DecisionMakingModel, param: DecisionParams): DecisionParams = {
    val groupSize = param.groupWeights.keySet.size + 1

    model match {
      case SelfishRoundup =>
        DecisionParams(
          (param.selfParams._1, param.selfParams._2, 1),
          param.groupPreferences,
          param.groupWeights.map(weights => weights._1 -> 0.0)
        )

      case EgalitarianRoundup =>
        DecisionParams(
          (param.selfParams._1, param.selfParams._2, 1),
          param.groupPreferences,
          param.groupWeights.map(weights => weights._1 -> 1.0)
        )

      case WeightedRoundup =>
        DecisionParams(
          (param.selfParams._1, param.selfParams._2, param.selfParams._3),
          param.groupPreferences,
          param.groupWeights
        )

      case SimpleSociotropyAutonomy(sociotropy, autonomy) =>
        DecisionParams(
          (param.selfParams._1, param.selfParams._2, 1.0),
          param.groupPreferences,
          param.groupWeights.map(weights => weights._1 -> 1.0)
        )

      case WeightedSociotropyAutonomy(sociotropy, autonomy) =>
        this normalize DecisionParams(
          (param.selfParams._1, param.selfParams._2, 1.0),
          param.groupPreferences,
          param.groupWeights.map(weights => weights._1 -> weights._2 * param.groupPreferences(weights._1))
        )

      case SocialImpactNSL => ???
    }
  }

  def normalize(param: DecisionParams): DecisionParams = {

    val factor = param.groupWeights.size / param.groupWeights.values.sum

    DecisionParams(
      (param.selfParams._1, param.selfParams._2, param.selfParams._3),
      param.groupPreferences,
      param.groupWeights.map(weights => weights._1 -> factor * weights._2)
    )
  }
}
