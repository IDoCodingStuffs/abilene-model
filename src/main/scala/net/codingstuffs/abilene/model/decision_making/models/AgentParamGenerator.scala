package net.codingstuffs.abilene.model.decision_making.models


import net.codingstuffs.abilene.model.decision_making.models.AgentParamGenerator.DecisionParams
import net.codingstuffs.abilene.model.decision_making.models.maslowian.MaslowianParamGenerator

import scala.util.Random

object AgentParamGenerator {

  final case class DecisionParams(selfParams: (String, Double, Double), groupPreferences: Map[String, Double], groupWeights: Map[String, Double])

}

class AgentParamGenerator(studyModel: AgentBehaviorModel, randomGenerators: (Random, Random)) {

  implicit var self: String = _
  implicit var memberNames: Set[String] = _

  val preferenceGenerator: Random = randomGenerators._1
  val weightsGenerator: Random = randomGenerators._2


  def getSelfParams(name: String): (String, Double, Double) =
    studyModel match {
      case MaslowianAgent => {
        val maslowianParams = new MaslowianParamGenerator(randomGenerators._1)
        (self,
          preferenceGenerator.nextDouble(),
          Math.pow(maslowianParams.getMaslowianSum(self), -1) * weightsGenerator.nextDouble())
      }
      case StochasticAgent => (self, preferenceGenerator.nextDouble(), weightsGenerator.nextDouble())
    }


  def groupPreferences(implicit groupMembers: Set[String]): Map[String, Double] =
    groupMembers.filter(member => member != self).map(member => member -> preferenceGenerator.nextDouble).toMap

  def groupWeights(implicit groupMembers: Set[String], max_deviation: Int = 3): Map[String, Double] =
    groupMembers.filter(member => member != self).map(member => member -> weightsGenerator.nextDouble).toMap

  def get: DecisionParams = DecisionParams(getSelfParams(self), groupPreferences, groupWeights)
}
